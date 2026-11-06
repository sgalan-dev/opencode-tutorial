# Diseño

## Contexto

El servicio es un monolito Spring Boot 3.3.5 con arquitectura hexagonal estricta (`domain.model`, `domain.port`, `domain.exception`, `application.usecase`, `infrastructure.adapter.input.rest`, `infrastructure.adapter.output.persistence`), Java 21, Liquibase como gestor de esquema y H2 in-memory como fuente de datos. `spring.jpa.hibernate.ddl-auto=validate` obliga a que el esquema de Liquibase coincida al 100% con el modelo JPA.

La jerarquía vigente es `Centro → Curso → Grupo → Estudiante`, con `Persona` como identidad global y `Professor` como la asociación persona ↔ centro. Cada agregado tiene su propio puerto de persistencia y su propio controlador, aunque las URLs los aniden bajo `Centro` (`GroupController` cuelga de `/api/v1/centers/{centerId}/courses/{courseId}/groups`, aunque `Course` y `Group` son agregados independientes).

Tres convenciones condicionan el diseño:

- **Las invariantes viven en el agregado y la base de datos pone la segunda capa.** `Group.assignTutor(Professor, Course)` rechaza un tutor de otro centro (`TutorInDifferentCenterException` → 409) mientras `student_group.tutor_professor_id` tiene `ON DELETE RESTRICT`; `Student.attachToGroup(Group, Course)` rechaza la matrícula cuando el centro del curso no es el del alumno mientras `student_group_link` lleva `UNIQUE (student_id, course_id)`.
- **La jerarquía de la URL es la cadena de pertenencia.** `HierarchyQueries` resuelve `center → course → group` y reporta como 404 un recurso que existe pero cuyo padre en la URL no lo posee.
- **Los roles cuelgan del hijo, no al revés, y se escriben en su endpoint.** El tutor es una propiedad del grupo (`Group.tutorProfessorId`) y se asigna con `POST|DELETE .../groups/{groupId}/tutor/{professorId}`; el `PUT` del grupo nunca lo toca, porque un `PUT` con sólo `name` lo desasignaría en silencio.

Ver `proposal.md` para la motivación y `specs/` para los requisitos.

## Goals / Non-Goals

**Goals:**

- Introducir `Subject` como agregado raíz con identidad propia, hija de un curso, con su CRUD completo anidado bajo la URL del curso.
- Materializar la impartición como una relación explícita curso ↔ asignatura ↔ profesor, en una tabla propia, tal como pide el prompt.
- Reutilizar sin desviaciones el patrón ya construido para el tutor del grupo: invariante en dos capas, endpoint dedicado para el rol, unicidad de nombre dentro del padre con 409 y guardas de borrado con 409.
- Mantener la dirección de dependencias `infrastructure → application → domain` y no añadir ninguna arista circular entre agregados.

**Non-Goals:**

- **No se implementa la impartición por defecto a cargo del tutor del grupo.** Es un desarrollo futuro explícito: una asignatura sin profesor es un estado válido y observable, y el sistema no calcula quién la dará.
- No se expone el detalle compuesto de la asignatura (`.../subjects/{subjectId}/detail`): el prompt no lo pide y el recurso normal ya expone el `professorId`.
- No se expone el listado inverso "asignaturas que imparte un profesor" fuera de la jerarquía de URLs pactada.
- No se reorganiza la rama `Personas` de la jerarquía: ya está hecha y este cambio no la toca.
- No se añaden atributos a la asignatura más allá del nombre (código, horas, tipo): el prompt define sólo `id` y `name`.
- No se introduce autenticación, autorización, paginación ni borrado lógico.

## Decisiones

### 1. `Subject` es un agregado raíz independiente, no una parte de `Course`

`Subject` tiene identidad propia (`SubjectId`), su propio puerto (`SubjectRepository`), su propio adaptador de persistencia y su propio controlador, exactamente como `Course` o `Group`, aunque su URL cuelgue de `/courses/{courseId}/subjects`. La relación con `Course` se modela con un identificador escalar (`courseId`), no con una asociación JPA, igual que `GroupJpaEntity.courseId`.

**Alternativas consideradas:**

- *`@ElementCollection` de asignaturas dentro de `CourseJpaEntity`* — rejected: convertiría el curso en un "god aggregate" que carga todas sus asignaturas y sus profesores en cada lectura, y no daría identidad propia a la asignatura.
- *Reutilizar el agregado `Group` con un discriminador de tipo* — rejected: la asignatura no tiene tutor, no tiene matrícula y su ciclo de vida es independiente del grupo.

### 2. La impartición es a nivel de curso, no de grupo

Una asignatura de un curso tiene **un** profesor, que es el especialista que la imparte en ese curso. La granularidad de grupo (cada grupo con su propio profesor para la misma asignatura) no se implementa: el prompt sitúa las asignaturas como hijas del curso y la URL base es `/courses/{courseId}/subjects`.

La consecuencia es que el fallback "si no hay profesor, lo da el tutor del grupo" queda íntegramente fuera: su enunciado ("el tutor *del grupo*") sólo tiene sentido cuando cada grupo puede tener una impartición distinta, es decir, en el modelo de nivel grupo. Aquí la asignatura sin profesor se queda simplemente sin profesor.

**Alternativas consideradas:**

- *Nivel de grupo desde ya* — rejected: cambiaría la URL base que fija el prompt, añadiría una dimensión a la relación de impartición y anticiparía un desarrollo que el prompt deja explícitamente para más adelante.
- *Nivel de curso ahora con `group_id` nullable ya en el esquema* — rejected: una columna que siempre vale `NULL` es ruido en el esquema y en el modelo; el cambio futuro puede añadirla con su propio changeset.

### 3. La impartición vive en una tabla propia, no en una columna de la asignatura

El prompt pide "un mecanismo (tabla) que nos relacione el curso con la asignatura y con el profesor que la imparte". El esquema queda:

```
subject                                  subject_teaching
  id            bigint PK autoinc           course_id    bigint FK -> course    (RESTRICT)
  course_id     bigint FK -> course          subject_id   bigint PK, FK -> subject (CASCADE)
  name          varchar(150) NOT NULL        professor_id bigint FK -> professor (RESTRICT)
  created_at    timestamp NOT NULL
  updated_at    timestamp NOT NULL
  UNIQUE (course_id, name) -> uk_subject_course_name
```

El `course_id` de `subject_teaching` está desnormalizado a propósito: es el mismo truco que `student_group_link.course_id` (desnormalizado para que el índice y la invariante "un grupo por curso" vivan en la misma tabla) y hace que la fila sea literalmente la relación curso ↔ asignatura ↔ profesor que pide el prompt. La `PK` sobre `subject_id` impone en la base de datos que una asignatura tiene **como mucho** un profesor, que es la segunda capa de la regla de dominio.

**Alternativas consideradas:**

- *Columna `professor_id` anulable en `subject`* — rejected: contradice el pedido explícito de una tabla de relación y cierra la puerta a la co-docencia o al histórico de imparticiones, que es justo lo que una tabla de relación permite. (Es, además, la solución que el prompt 02 aceptó para el tutor del grupo; aquí el prompt dice "tabla" y no "columna", y el cambio de palabra es deliberado.)
- *`subject` como catálogo global + tabla de pertenencia `course_subject` + tabla de impartición* — rejected: la asignatura perdería su identidad propia (la URL `/subjects/{subjectId}` resolvería por el par curso-asignatura, no por un identificador), y habría que decidir qué significa "borrar del catálogo" frente a "quitar del curso", una ambigüedad que no existe en el modelo actual.

### 4. El profesor es una propiedad del agregado `Subject`, aunque se persista en otra tabla

`Subject` lleva `professorId` anulable y expone `assignProfessor(Professor, Course)` y `unassignProfessor()`, con la misma gramática que `Group.tutorProfessorId` / `assignTutor` / `unassignTutor`. El agregado, por tanto, "sabe" su profesor; lo que ocurre es que la fila que lo guarda está en otra tabla.

La consecuencia asumida es que `SubjectPersistenceAdapter` es el único que conoce el reparto en dos tablas: escribe la fila de `subject` y la de `subject_teaching` en la misma transacción, y al leer rehidrata el `professorId` desde la fila de impartición. Es un coste acotado a un adaptador, y a cambio la API no necesita una segunda consulta ni un agregado auxiliar.

**Alternativas consideradas:**

- *Un agregado `TeachingAssignment`/`Imparticion` separado, con identidad = `subject_id`* — rejected: duplica la noción de "el profesor de la asignatura" en dos sitios (el agregado y la tabla), obliga a un segundo agregado y a un segundo caso de uso para leer algo que es un atributo de la asignatura. Además, el listado de asignaturas de un curso Passing a dos consultas o a un join manual, con el riesgo de N+1.
- *Dejar el `professorId` fuera del dominio y leerlo siempre desde la tabla de impartición* — rejected: el dominio quedaría sin la invariante (el agregado no podría rechazar por sí mismo un profesor de otro centro).

### 5. La invariante "el profesor es del centro del curso" se resuelve en dos capas

`Subject` conoce su `courseId` pero no su centro; el centro de una asignatura es el de su curso. Por eso `assignProfessor` recibe el `Course` completo, igual que `Group.assignTutor` recibe el `Course` completo:

```java
public void assignProfessor(Professor professor, Course course) {
    // el curso debe ser el curso de la asignatura  (guardia de programación)
    // el centro del curso debe ser el centro del profesor  (invariante)
    this.professorId = professor.id();
    touch();
}
```

La aplicación resuelve el profesor **acotado al centro de la URL** (`hierarchy.requireProfessor(centerId, professorId)`), de modo que un profesor de otro centro no es alcanzable bajo esa URL y la respuesta es 404, igual que un curso pedido bajo un centro que no lo posee. Dentro de la cadena `center → course → subject`, `requireSubject` garantiza que el curso pertenece al `centerId` de la URL, de modo que "centro de la asignatura" y "centro de la URL" coinciden. El 409 de la guarda de dominio queda como red de seguridad para las invocaciones que no pasan por la jerarquía, con `SubjectProfessorInDifferentCenterException`.

Es el mismo patrón de doble capa que `TutorInDifferentCenterException` + `fk_group_tutor_professor`.

### 6. La unicidad del nombre dentro del curso se aplica en dominio y en base de datos

El nombre de la asignatura es único **dentro del curso** (no global): dos cursos distintos pueden tener "Matemáticas". El agregado no puede saber si ya existe otra asignatura con ese nombre en el curso (no ve el repositorio), así que el chequeo vive en el servicio de aplicación, con el `DuplicateNameException` que ya usan `CreateCourseService` y `UpdateGroupService`, y se refleja en la base de datos con `UNIQUE (course_id, name)`.

La comprobación debe aplicarse también en el `PUT`, que puede renombrar la asignatura a un nombre ya usado.

### 7. El `PUT` de la asignatura no toca el profesor

La asignación del profesor tiene endpoints propios, `POST|DELETE .../subjects/{subjectId}/professor/{professorId}`, en espejo de `POST|DELETE .../groups/{groupId}/tutor/{professorId}`.

El motivo es el mismo que motivó la decisión 3 del diseño anterior: el `PUT` es semántica de reemplazo total, y hoy `UpdateGroupService` sólo renombra el agregado cargado. Si `professorId` entrara en `SubjectRequest`, el campo o bien se ignoraría —el cliente creería que ha asignado y no habría pasado nada— o bien se aplicaría, y entonces un `PUT` con sólo `name` **desasignaría el profesor en silencio**. El `POST` de creación tampoco acepta `professorId`: la creación es un alta y la asignación es una operación posterior y explícita.

El `POST` de asignación **reemplaza** al profesor anterior (es la escritura de un sub-recurso singular: "el profesor *de esta asignatura* pasa a ser este"), y tanto el `POST` como el `DELETE` son idempotentes: asignar el profesor que ya tiene o desasignar uno que no es el actual son no-ops con 204, igual que ocurre con el tutor.

**Alternativas consideradas:**

- *Campo `professorId` en el payload del `POST` de creación* — rejected: obligaría a tener dos mecanismos para asignar (el del alta y el dedicado) con semánticas de null distintas; la simetría con el grupo pierde y no gana ningún caso real, porque crear la asignatura y asignarle el profesor son dos hechos de negocio distintos.

### 8. Los borrados con dependientes se rechazan con 409, y la guarda vive en la aplicación

- `DeleteCourseService` cuenta ahora también las asignaturas del curso y lanza `ResourceHasDependentsException("Course", id, "N subject(s)")`, con el patrón exacto que ya usa para los grupos.
- `DeleteProfessorService` cuenta las asignaturas que el profesor imparte y lanza `ResourceHasDependentsException("Professor", id, "N subject(s)")`. Para eso hace falta `SubjectRepository.findByProfessorId(ProfessorId)`, que **no** se expone en ningún endpoint: es exclusivamente una guarda de escritura, igual que `GroupRepository.findByTutorProfessorId` lo es para el tutor.
- `DeleteCenterService` **no** cambia: un centro con asignaturas tiene cursos, y un centro con cursos ya se rechaza con 409. La guarda es transitiva y no hace falta añadir un contador nuevo.

En la base de datos, `fk_teaching_professor` es `ON DELETE RESTRICT` y `fk_subject_course` es `ON DELETE RESTRICT`: el borrado del curso con asignaturas y el del profesor que imparte se cortan también en la base de datos, como red de seguridad frente a carreras.

### 9. `subject` es el nombre natural de la tabla

`GROUP` es palabra reservada y por eso la tabla del grupo se llama `student_group`. `SUBJECT` **no** es palabra clave de H2: se ha comprobado en el mapa de palabras clave del propio H2 2.2.224 (`org.h2.util.ParserUtil`), donde no aparece ni como reservada ni como no reservada. La tabla se llama por tanto `subject`, sin apodo, igual que `course` o `professor`.

### 10. Los cambios de Liquibase son incrementales, con changesets nuevos

Se sigue la regla del `AGENTS.md`: los cambios de esquema entran en changesets nuevos, nunca reescribiendo los existentes.

```
db/changelog/006-create-subjects.yaml    012-create-subject
                                        013-create-subject-teaching
db/changelog/007-seed-subjects.yaml      113-seed-course-center-2
                                        114-seed-subjects
                                        115-seed-subject-teaching
```

Los identificadores libres son `012` (los de `001`–`005` used el `001`–`006` y el `101`–`105`, más el `008`–`011`) y `113` (los de semilla usan el `101`–`112`). El orden importa: la tabla `subject` debe existir antes de que `subject_teaching` la referencie.

### 11. Los datos de ejemplo cubren los casos que hay que poder ver a ojo

```
Centro 1 "Centro de prueba 1"
  Curso 1 "Primero Primaria"
    Materia "Matemáticas"  -> profesor 1 (Alan Turing @ centro 1)   <- asignatura CON profesor
    Materia "Lengua"       -> sin profesor                          <- asignatura SIN profesor
  Curso 2 "Segundo Primaria"
    Materia "Matemáticas"  -> sin profesor                          <- mismo nombre, otro curso
                                (su Grupo C ya tiene tutor: preparado para el fallback futuro)
Centro 2 "IES Segundo centro"
  Curso 3 "Tercero Primaria"
    Materia "Ciencias"      -> profesor 2 (Alan Turing @ centro 2)   <- profesor del centro del curso
```

Así quedan visibles los cuatro casos: asignatura con profesor, asignatura sin profesor, el mismo nombre de asignatura en dos cursos distintos, y el profesor que pertenece al centro del curso que la imparte. El 409 por nombre duplicado y el 409 al borrar un curso o un profesor con dependientes no se siembran: son comportamiento, no dato.

## Riesgos / Trade-offs

- **[El `course_id` desnormalizado de `subject_teaching` puede quedar obsoleto]** → Sólo sería un problema si una asignatura pudiera cambiar de curso. Hoy eso no está expuesto (igual que `Group.moveToCourse`, que existe en el dominio pero ningún caso de uso invoca). Si en el futuro se expone el traslado, la fila de impartición tendrá que moverse en la misma transacción.
- **[Un agregado escrito en dos tablas puede desincronizarse]** → Hay un único escritor: `SubjectPersistenceAdapter`, dentro de la misma transacción, y la FK `fk_teaching_subject` va con `ON DELETE CASCADE`, de modo que borrar la asignatura se lleva su fila de impartición. No hay ninguna otra ruta que escriba en `subject_teaching`.
- **[El listado de asignaturas necesita dos consultas (asignaturas + imparticiones del curso)]** → Es aceptable: se resuelve cargando de una vez todas las imparticiones del curso y emparejándolas en memoria, sin N+1. Con el tamaño de datos de este servicio, el impacto es nulo.
- **[El 404 de "profesor de otro centro" oculta el conflicto]** → Es la misma semántica que ya tiene el sistema para el tutor del grupo, para los cursos y para los estudiantes: un recurso que no es alcanzable bajo ese padre se reporta como inexistente. El 409 del dominio queda como red de seguridad.
- **[`ddl-auto=validate` bloquea el arranque si el esquema y las entidades no coinciden]** → Las entidades JPA y los changesets entran en el mismo cambio; la verificación incluye `mvn spring-boot:run` antes de cerrar.
- **[El `PUT` de la asignatura no puede cambiar el profesor]** → Un cliente que espere gestionarlo desde el recurso de la asignatura no lo encontrará. → Es explícito en el spec (el `PUT` preserva el profesor) y la ruta de asignación queda documentada en OpenAPI.
- **[El borrado de un profesor requiere un método de lectura inverso que la API no expone]** → `SubjectRepository.findByProfessorId` existe sólo para la guarda de borrado, y queda documentado en el puerto.

## Plan de migración

No hay migración en sentido tradicional: no hay datos persistidos más allá del H2 local, que se recrea en cada arranque. El flujo esperado tras el cambio:

1. Spring Boot levanta y configura las entidades JPA.
2. Liquibase aplica `001`–`007` sobre la base H2 recién creada: la tabla `subject` con su unicidad, la tabla `subject_teaching` con sus tres claves foráneas y los datos de ejemplo.
3. Hibernate valida las entidades contra el esquema (operación barata, porque H2 se acaba de crear).
4. La aplicación queda lista con los datos demo: 2 centros, 3 cursos, 3 grupos, 4 estudiantes, 5 personas, 3 profesores, 4 asignaturas (2 con profesor y 2 sin profesor).

**Rollback:** `git revert` del commit. Como el esquema se reconstruye en cada arranque desde los changesets, no hay estado residual que limpiar.

## Open Questions

- *Si el detalle de la asignatura debe acabar mostrando el profesor con los datos de su persona*, como hacen el detalle del grupo y el del profesor. La decisión actual es no: `SubjectResponse` expone el `professorId` y nada más.
- *Si la impartición debe pasar a nivel de grupo* cuando se implemente el fallback "el tutor del grupo imparte la asignatura". La tabla actual no lo impide (bastaría con añadir `group_id` y su índice por grupo), pero es otro cambio, con su propio delta de specs.
- *Si el `Purpose` de los specs materializados de `courses` y `professors` debe mencionar las asignaturas.* Al archivar este cambio, esos textos deberían mencionar la relación: el curso "agrupa grupos y asignaturas" y el profesor "imparte asignaturas". Es un ajuste de documentación de los specs, no del comportamiento.
- *Atributos de la asignatura más allá del nombre* (código, horas semanales, tipo). El prompt define sólo `id` y `name`; queda para un cambio propio.
