# Diseño

## Contexto

El servicio es un monolito Spring Boot 3.3.5 con arquitectura hexagonal estricta (`domain.model`, `domain.port`, `domain.exception`, `application.usecase`, `infrastructure.adapter.input.rest`, `infrastructure.adapter.output.persistence`), Java 21, Liquibase como gestor de esquema y H2 in-memory como fuente de datos. `spring.jpa.hibernate.ddl-auto=validate` obliga a que el esquema de Liquibase coincida al 100% con el modelo JPA.

La jerarquía vigente es `Centro → Curso → Grupo → Estudiante`, con `Persona` como identidad global fuera de ella. Cada agregado tiene su propio puerto de persistencia y su propio controlador, aunque las URLs los aniden bajo `Centro` (`GroupController` cuelga de `/api/v1/centers/{centerId}/courses/{courseId}/groups`, aunque `Course` y `Group` son agregados independientes).

Dos convenciones condicionan el diseño:

- **Las invariantes viven en el agregado y la base de datos pone la segunda capa.** `Student.attachToGroup(Group, Course)` rechaza la matrícula cuando el centro del curso no es el del alumno (`StudentInDifferentCenterException` → 409) mientras `student_group_link` lleva `UNIQUE (student_id, course_id)` para las carreras. `DeleteCenterService` comprueba los dependientes a mano y devuelve 409 con un mensaje intelligible, en vez de delegar en la violación de la FK.
- **La jerarquía de la URL es la cadena de pertenencia.** `HierarchyQueries` resuelve `center → course → group` y reporta como 404 un recurso que existe pero cuyo padre en la URL no lo posee.

Ver `proposal.md` para la motivación y `specs/` para los requisitos.

## Goals / Non-Goals

**Goals:**

- Introducir `Professor` como la asociación persona ↔ centro, con su CRUD completo y su unicidad `(person_id, center_id)` en dos capas.
- Modelar el tutor como una propiedad opcional del grupo, con la invariante "el tutor es un profesor del centro del grupo" resuelta en el dominio.
- Mantener la dirección de dependencias `infrastructure → application → domain`: el dominio no conoce la persistencia ni lanofiguración web, y los agregados no se referencian entre sí de forma circular.
- Ampliar las respuestas de grupo para que el tutor sea observable sin exposing consultas adicionales al cliente.

**Non-Goals:**

- No se modela ningún atributo de profesor más allá de persona y centro (materias, departamento, código de empleado): las asignaturas son otro cambio.
- No se expone el listado inverso "profesores de una persona", que atravesaría la frontera de centros y queda fuera de la jerarquía de URLs pactada.
- No se cambia la URLs existentes ni se elimina ningún endpoint; `GroupResponse` y `GroupDetailResponse` sólo growen.
- No se introduce autenticación, autorización, paginación ni borrado lógico.
- No se toca `Person`: sigue siendo una identidad global, sin centro.

## Decisiones

### 1. `Professor` es un agregado independiente, no una parte de `Center` ni de `Person`

`Professor` tiene identidad propia (`ProfessorId`), su propio puerto (`ProfessorRepository`) y su propio controlador, exactamente como `Course` o `Group`, aunque sus URLs cuelguen de `/centers/{centerId}/professors`. La relación con `Center` y con `Person` se modela con identificadores escalares (`centerId`, `personId`), no con associations JPA, igual que `GroupJpaEntity.courseId`.

**Alternativas consideradas:**

- *Columna `professor` dentro de `CenterJpaEntity` como `@OneToMany`* — rejected: convertiría el centro en un "god aggregate" con la carga de todo su claustro y obligaría a que cada profesor cargase su centro entero.
- *Reutilizar `Student` con un discriminador de rol* — rejected: el estudiante y el profesor tienen ciclos de vida distintos (un profesor puede no estar matriculado en ningún grupo; un estudiante no puede tutorizar) y la unicidad es por par persona-centro, no por persona.

### 2. La referencia al tutor la guarda `Group`, no `Professor`

`Group` gana un `tutorProfessorId` anulable. `Professor` no sabe nada de los grupos: no hay ninguna arista que vaya de profesor a grupo. Esto mantiene el grafo de agregados acíclico y hace que "quién tutoriza qué" sea una consulta de lectura, no un invariante del agregado del profesor.

**Consecuencia asumida:** el detalle del profesor **no** lista los grupos tutelados (decisión del usuario durante la exploración). Por tanto la API no expone la relación inversa, y el único punto donde se necesita es la guarda de borrado descrita en la decisión 7.

**Alternativa considerada:** que `Professor` expusiera `Set<GroupId> tutoredGroupIds` en su estado, replicando el patrón de `Student.enrolments`. — rejected: obliga a que cada cambio de tutor actualice dos agregados y a mantener sincronizadas dos copias de la misma relación, con el riesgo de divergencia que ya suffers `student_group_link.course_id`.

### 3. La asignación de tutor tiene endpoints propios, no un campo en el `PUT` del grupo

`POST`/`DELETE /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/tutor/{professorId}`, en espejo de las operaciones de matrícula `.../groups/{groupId}/students/{studentId}` que ya existen en `GroupController`.

El motivo concreto: `PUT /groups/{groupId}` es semántica de reemplazo total y hoy `UpdateGroupService` sólo renombra el agregado cargado (no lo reconstruye). Si `tutorProfessorId` entrara en `GroupRequest`, el campo o bien se ignoraría —el cliente creería que ha asignado y no ha pasado nada— o bien se aplicaría, y entonces un `PUT` con sólo `name` **desasignaría el tutor en silencio**. Un endpoint dedicado elimina la ambigüedad del `null` ausente frente al `null` explícito.

**Alternativas consideradas:**

- *Sólo el campo `tutorProfessorId` en el `PUT` del grupo* — rejected por la ambigüedad anterior.
- *Sólo el campo `tutorProfessorId` en el `POST` de creación del grupo* — rejected: deja sin tutor a todo grupo creado después de la asignación inicial y no cubre el caso normal (asignar y cambiar tutores).

El `PUT` del grupo conserva su semántica actual: cambia el nombre y **no** toca el tutor. El spec lo fija con un escenario explícito.

### 4. `POST` sobre el tutor reemplaza al tutor anterior

Asignar un tutor es la escritura de una sub-recurso singular: "el tutor *de este grupo* pasa a ser este profesor". El `POST` idempotente (asignar el tutor actual es un no-op) y el `DELETE` idempotente (desasignar un tutor que no es el actual es un no-op) devuelven ambos 204, en coherencia con `POST`/`DELETE .../students/{studentId}`.

**Alternativa considerada:** rechazar con 409 la reasignación, obligando a desasignar antes. — rejected: deja el grupo sin tutor en un estado intermedio y encarece un cambio de criterio sin ganancia real.

### 5. La invariante "el tutor es profesor del centro del grupo" se resuelve en dos capas

El `Group` conoce su `courseId` pero no su centro; el centro de un grupo es el de su curso. Por eso `assignTutor` recibe el `Course` completo, igual que `Student.attachToGroup(Group, Course)` lo hace para validar el centro del alumno:

```java
public void assignTutor(Professor tutor, Course course) {
    // el curso debe ser el curso del grupo  (guardia de programación)
    // el centro del curso debe ser el centro del profesor  (invariante)
    this.tutorProfessorId = tutor.id();
    touch();
}
```

La aplicación, por su parte, resuelve el profesor **acotado al centro de la URL** (`hierarchy.requireProfessor(centerId, professorId)`), de modo que un profesor de otro centro no es alcanzable bajo esa URL y la respuesta es 404, igual que un curso pedido bajo un centro que no lo posee. Dentro de la cadena `center → course → group`, `requireGroup` ya garantiza que el curso pertenece al `centerId` de la URL, de modo que "centro del grupo" y "centro de la URL" coinciden.

Resultado: el 404 lo produce la regla de la casa (recurso no alcanzable bajo su padre) y el 409 de la guarda de dominio queda como red de seguridad para las invocaciones que no pasan por la jerarquía. Es el mismo patrón de doble capa que `StudentInDifferentCenterException` + `uk_student_course`.

**Alternativa considerada:** que la comprobación viviera sólo en la aplicación. — rejected: las invariantes viven en el agregado; una ruta futura que no use `HierarchyQueries` se saltaría el control.

### 6. La unicidad `(person_id, center_id)` se aplica en dominio y en base de datos

El agregado `Professor` no puede saber si ya existe otro profesor de la misma persona en el centro (no ve el repositorio), así que el chequeo vive en el servicio de aplicación, como el `DuplicateNameException` que ya usa `UpdateGroupService`. Se añade una excepción de dominio propia, `DuplicateProfessorInCenterException`, mapeada a 409, y una `UNIQUE (person_id, center_id)` en la tabla como segunda capa, con el `DataIntegrityViolationException` traducido a 409.

La comprobación debe aplicarse también en el `PUT`, que puede re-apuntar el `personId` del profesor a otra persona.

### 7. Borrar un profesor con grupos tutelados se rechaza con 409

`DeleteProfessorService` comprueba los grupos tutelados y lanza `ResourceHasDependentsException("Professor", id, "2 group(s) as tutor")`, con el patrón exacto de `DeleteCenterService`. La FK `fk_group_tutor_professor` es `ON DELETE RESTRICT` y actúa de red de seguridad frente a carreras.

Para poder hacer ese recuento hace falta `GroupRepository.findByTutorProfessorId(ProfessorId)`. Ese método **no** se expone en ningún endpoint: es exclusivamente una guarda de escritura, de modo que la decisión de no mostrar los grupos tutorizados en el detalle del profesor se respeta.

**Alternativas consideradas:**

- *`ON DELETE SET NULL`* — rejected: los grupos se quedarían sin tutor en silencio y el cliente no se enteraría de la pérdida.
- *Borrado en cascada de los grupos* — rejected: el tutor es un dato de negocio, no una fila de enlace. En este proyecto la cascada sólo se usa para `student_group_link`, que es estructura.

Como efecto colateral, `DeleteCenterService` debe comprobar también los profesores del centro: sin ese chequeo, `fk_professor_center` reventaría contra la base de datos y devolvería un `DataIntegrityViolationException` crudo en lugar del 409 con mensaje intelligible. `DeletePersonService` no necesita cambios: delega en `fk_student_person` y se comporta igual con `fk_professor_person`.

### 8. `tutor_professor_id` es una columna escalar, no una asociación JPA

Igual que `course_id` en `GroupJpaEntity`, se guarda como `Long` con el identificador plano. El dominio sólo necesita el identificador; cargar la fila del profesor al leer un grupo evitaría una consulta adicional innecesaria en el listado de grupos. El `PersonSummaryResponse` del tutor se resuelve en la capa de aplicación, en el caso de uso de detalle.

La tabla nueva se llama `professor`: no es palabra reservada en SQL ni en H2, así que no necesita el sufijo que usa `student_group` (`GROUP` sí lo es).

### 9. Los cambios de Liquibase son incrementales, no una reescritura

El cambio anterior (`add-centers-and-courses-hierarchy`) se aceptó reescribir `001` y `002` desde cero, justificado en que la fuente de datos es H2 in-memory y se recrea en cada arranque. La decisión 5 de aquel diseño ya anticipaba que a partir de la siguiente iteración se volvía a la regla del `AGENTS.md`: **los cambios de esquema son incrementales, en changesets nuevos**. Este cambio aplica esa regla.

```
db/changelog/004-create-professor.yaml   009-create-professor
                                        010-add-group-tutor-column
                                        011-add-group-tutor-fk
db/changelog/005-seed-professors.yaml    110-seed-center-2
                                        111-seed-professors
                                        112-assign-group-tutors
```

El orden importa: la tabla `professor` debe existir antes de añadir la FK desde `student_group`. El identificador `008` ya lo usa `003-add-person-surname2`, de modo que el siguiente libre es el `009`.

### 10. Los datos de ejemplo cubren los tres casos que hay que poder ver

La semilla actual tiene un centro, dos cursos, tres grupos, cuatro personas y cuatro estudiantes. Para que las reglas nuevas se puedan comprobar a ojo se añaden:

```
Centro 2 "IES Segundo centro"            -> id 2
Profesor: persona 4 (Alan Turing)  @ centro 1 -> id 1
Profesor: persona 4 (Alan Turing)  @ centro 2 -> id 2   <- misma persona, dos centros
Profesor: persona 5 (Katherine J.)  @ centro 1 -> id 3
Grupo A (id 1) -> tutor = profesor 1
Grupo C (id 3) -> tutor = profesor 3
Grupo B (id 2) -> sin tutor                      <- columna nula / detalle sin tutor
```

Así quedan visibles: el profesor multi-centro, el grupo con tutor y el grupo sin tutor.

## Riesgos / Trade-offs

- **[El invariante tutor⊂centro se rompe si el grupo cambia de centro]** → El centro de un grupo se deriva del de su curso, y tanto `Group.moveToCourse(CourseId)` como `Course.moveToCenter(CenterId)` existen en el dominio aunque hoy ningún caso de uso los invoca. Si se exponen en el futuro, un grupo con tutor podría pasar a un curso de otro centro conservando un tutor que ya no es válido. → Mitigación: `moveToCourse` y `moveToCenter` no se exponen en este cambio; cuando se expongan, el agregado deberá rechazar el movimiento mientras el grupo tenga tutor o estudiantes, siguiendo el patrón de `GroupHasStudentsException`.
- **[Carrera entre el chequeo del dominio y la base de datos]** → Dos peticiones concurrentes podrían pasar el chequeo de unicidad y ambas llegar al `save`. → Mitigación: la `UNIQUE (person_id, center_id)` y la FK `ON DELETE RESTRICT` cortan la carrera en la base de datos, y el `DataIntegrityViolationException` se traduce a 409 con el mismo mensaje que la capa de dominio.
- **[`ddl-auto=validate` bloquea el arranque si el esquema y las entities no coinciden]** → Una columna nueva en `student_group` o una tabla nueva sin su entity bloquean el boot. → Mitigación: los changesets y las entidades JPA entran en el mismo cambio, con un `mvn compile` y un arranque manual de `mvn spring-boot:run` antes de cerrar.
- **[El 404 de "profesor de otro centro" oculta el conflicto]** → Un cliente que asigne por error un profesor de otro centro recibe un 404 que no dice "ese profesor es de otro centro". → Mitigación: es la misma semántica que ya tiene el sistema para cursos y estudiantes; el mensaje de 404 referencia el recurso, y el servicio de aplicación puede distinguir el caso para registrarlo.
- **[El `PUT` del grupo no puede cambiar el tutor]** → Un cliente que espere gestionarlo desde el recurso del grupo no lo encontrará. → Mitigación: es explícito en el spec (el `PUT` preserva el tutor) y la ruta de asignación está documentada en OpenAPI.
- **[La guarda de borrado necesita un método de lectura inverso]** → `GroupRepository.findByTutorProfessorId` existe aunque la API no exponga la relación inversa. → Mitigación: queda justificado por su único uso (la guarda de borrado) y documentado en el puerto.
- **[Los identificadores de la semilla dependen del orden de inserción]** → Un `INSERT` que se cuele antes desplaza los ids y rompe las referencias. → Mitigación: los ids se fijan de forma explícita en los changesets, igual que hace ya `002-seed-data.yaml`.

## Plan de migración

No hay migración en el sentido tradicional (no hay datos persistidos más allá del H2 local). El flujo esperado al arrancar la aplicación tras el cambio:

1. Spring Boot levanta; Hibernate configura sus entidades.
2. Liquibase aplica `001`, `002`, `003`, `004` y `005` sobre la base H2 recién creada: tabla `professor`, columna `tutor_professor_id` con su FK, y los datos de ejemplo.
3. Hibernate valida las entidades contra el esquema (operación barata porque H2 se acaba de crear).
4. La aplicación queda lista con los datos demo: 2 centros, 2 cursos, 3 grupos, 4 estudiantes, 4 personas y 3 profesores.

**Rollback:** `git revert` del commit. Como el esquema se reconstruye en cada arranque desde los changesets, no hay estado residual que limpiar.

## Open Questions

- *Si el detalle del profesor debe acabar listando los grupos tutelados.* La decisión actual es no; si en el futuro se quiere, la consulta ya está disponible en `GroupRepository` y bastaría con ampliar la vista de detalle y el spec.
- *El `Purpose` de los specs materializados de `centers` y `groups`.* Al archivar este cambio, esos textos deberían mencionar a los profesores como hijos del centro y al tutor como parte del grupo; es un ajuste de documentación de los specs, no del comportamiento.
- *Atributos de profesor más allá de persona y centro* (materias, departamento, código de empleado). Queda para el cambio de asignaturas.
