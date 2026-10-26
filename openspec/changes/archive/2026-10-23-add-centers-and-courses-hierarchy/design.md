# Diseño

## Contexto

La aplicación es un servicio Spring Boot 3.3.5 monolítico con arquitectura hexagonal estricta (paquetes `domain.model`, `domain.port`, `domain.exception`, `application.usecase`, `infrastructure.adapter.input.rest`, `infrastructure.adapter.output.persistence`, `infrastructure.config`), Java 21, Liquibase como gestor de cambios de esquema y H2 in-memory como fuente de datos. `spring.jpa.hibernate.ddl-auto=validate` obliga a que el esquema gestionado por Liquibase coincida al 100% con el modelo JPA. La motivacion del cambio está en `proposal.md`. Los contratos externos se expresan en `specs/centers/spec.md`, `specs/courses/spec.md`, `specs/groups/spec.md` y `specs/students/spec.md`.

## Goals / Non-Goals

**Goals:**
- Reorganizar la jerarquía persistente y la superficie REST bajo Centro y Curso sin perder las reglas de negocio que ya existían (Persona global, grupo ↔ estudiante N:M, sin setters públicos en agregados).
- Hacer cumplir la invariante "como mucho un grupo por curso" en dos capas (dominio + BD) con un solo punto de mantenimiento.
- Mantener las convenciones existentes: value objects con constructor compacto que enforce invariantes; puertos sin frameworks; adaptadores dueños únicos del mapping <-> JPA; `ApiExceptionHandler` único que mapea excepciones a HTTP; documentación OpenAPI en cada endpoint.

**Non-Goals:**
- AuthN/AuthZ, paginación, HATEOAS, versionado por cabeceras, multi-tenancy, soporte de borrado lógico.
- Reglas de negocio adicionales sobre cursos (capacidad, fechas, requisitos) más allá de las explícitas en los specs.
- Cambios sobre `Person` (sigue siendo un agregado independiente y global).

## Decisiones

### 1. Tabla de enlace `student_group_link` denormaliza `course_id`

La invariante "un estudiante, un grupo por curso" se enforce en BD con `UNIQUE (student_id, course_id)` sobre `student_group_link`. Como la tabla no conoce directamente el `course_id` del grupo, este atributo se **denormaliza** en cada fila de enlace. El `StudentPersistenceAdapter` lo rellena a partir del `Group.cursoId()` en el momento del `save`. La tabla `student_group` sigue siendo la fuente de verdad del curso al que pertenece un grupo; el denormalizado sirve sólo para el índice.

**Alternativas consideradas:**
- *Índice único por expresión SQL* (`UNIQUE(student_id, (SELECT course_id FROM student_group WHERE ...))`) — rechazado por portabilidad: H2 no soporta índices funcionales como MySQL/Postgres.
- *Constraint lógica vía trigger* — rechazado por opacidad y dificultad de testing.
- *Tabla de enlace como entidad propia* (`Enrollment`) en vez de `@ElementCollection` — viable pero introduce más código del necesario para esta iteración; se mantiene `@ElementCollection` sobre `Set<Long> groupIds` y se documenta la denormalización de `course_id`.

### 2. La invariante vive en el dominio (`Student.attachToGroup(Group)`)

La firma pasa de `attachToGroup(GroupId)` a `attachToGroup(Group)`. El agregado recibe el `Group` completo para poder inspeccionar su `cursoId()` y decidir si la inscripción romple la regla. Si la rompe lanza `StudentAlreadyEnrolledInCourseException`, mapeada a `409 Conflict` en `ApiExceptionHandler`.

**Alternativa considerada:** mantener `GroupId` y resolver el curso dentro del servicio de aplicación. Rechazada por coherencia con el resto del modelo (todas las invariantes viven en el agregado; los servicios orquestan, no deciden).

### 3. Nuevo contrato de `AssignStudentToGroupService`

Antes: cargaba el `Group` solo para `existsById` y luego llamaba a `student.attachToGroup(groupId)`. Ahora: carga el `Group` completo (`findById`), valida que exista (lanza `GroupNotFoundException`), valida que el grupo pertenece al curso y centro de la URL (lanza `CourseNotFoundException` o `CenterNotFoundException` si toca), valida que el estudiante pertenece al mismo centro (lanza `StudentNotFoundException` o nueva `StudentInDifferentCenterException` → 409), y pasa el `Group` al agregado. El servicio también es responsable de detectar y traducir un `DataIntegrityViolationException` que se cuele hasta el adaptador (carrera contra la BD) en `409 Conflict`.

### 4. Endpoints de detalle como casos de uso de lectura específicos

`GET .../groups/{groupId}/students/detail` y `GET .../students/{studentId}/detail` no son mappings 1:1 de un agregado — ensamblan datos de varios. Se implementan como casos de uso de aplicación (`GetGroupDetailUseCase`, `GetStudentDetailUseCase`) que orquestan los puertos necesarios (`GroupRepository`, `StudentRepository`, `PersonRepository`, `CenterRepository`, `CourseRepository`) y devuelven un DTO de vista ensamblado ubicado en `infrastructure.adapter.input.rest.dto`. El "ensamblado" se queda en la capa de aplicación, no en el controlador (mantiene el controlador como mero adaptador HTTP).

### 5. Reescritura libre de `001` y `002`

El esquema se reescribe entero en `001-create-schema.yaml`. La regla "no tocar cambios ya aplicados" del `AGENTS.md` se relaja explícitamente para esta iteración porque H2 in-memory se recrea en cada arranque y no hay datos persistidos en producción. `002-seed-data.yaml` también se reescribe para sembrar 1 Centro ("IES Demo"), 1 Curso ("2026-2027") y reubicar los 3 grupos y 4 estudiantes existentes bajo esa jerarquía. `003-add-person-surname2.yaml` se mantiene tal cual (incorpora la columna `surname2` a la `person`). `db.changelog-master.yaml` y `application.yml` no se tocan.

### 6. Nuevos puertos en `domain.port`

`CenterRepository` y `CourseRepository` con la misma forma que `GroupRepository` y `StudentRepository`: `findById`, `findAll` (con filtros por padre cuando aplique), `save`, `deleteById`, `existsById`. Para listar `Course`s de un `Center` y `Group`s de un `Course` los métodos toman el id del padre (`findByCenterId`, `findByCourseId`) para empujar la查询 al adaptador y dejar la capa de dominio agnóstica de JPA. Para listar estudiantes por grupo se reutiliza la información que ya carga el adaptador de estudiante (`Student.groupIds()`).

### 7. Validaciones Bean Validation + value objects compactos

Las validaciones de "no blank", "longitud máxima", "no nulo" se reparten entre anotaciones `@NotBlank`, `@Size`, `@NotNull`, `@Positive` en los DTOs y los compactos de los value objects (`CenterName`, `CourseName`, `GroupName` ya existente). `ApiExceptionHandler` ya mapea ambos caminos (`MethodArgumentNotValidException` → 400, `IllegalArgumentException` → 400); las nuevas excepciones de dominio se añaden con handlers dedicados.

### 8. Mapeo de excepciones a HTTP

| Excepción | HTTP |
|---|---|
| `CenterNotFoundException`, `CourseNotFoundException`, `GroupNotFoundException`, `StudentNotFoundException`, `PersonNotFoundException` | 404 |
| `StudentAlreadyEnrolledInCourseException` | 409 |
| `DataIntegrityViolationException` con causa de violación `uk_student_course` | 409 |
| `MethodArgumentNotValidException`, `IllegalArgumentException` | 400 |
| Existencia de FK violada (centro, curso, persona, grupo referenciados por URL no existen) | 404 (no 409) para mantener semántica coherente con "el padre no existe" |

## Riesgos / Trade-offs

- **[Carrera dominio vs BD]** El dominio chequea la regla antes de guardar; entre el chequeo y el `save` otro hilo podría insertar una fila conflictiva. → Mitigación: la UNIQUE en BD detecta el conflicto y `StudentPersistenceAdapter` traduce la `DataIntegrityViolationException` a `StudentAlreadyEnrolledInCourseException` → 409 con el mismo mensaje.
- **[Denormalización `course_id` puede divergir del real]** Si un `Group` cambia de curso (`moveToCourse`) sin reescribir el `course_id` denormalizado en sus enlaces. → Mitigación: el método de cambio de curso del agregado también invalida la caché del adaptador, o se prohíbe `moveToCourse` mientras tenga estudiantes; o se documenta la prohibición como parte del comportamiento. Decisión recomendada: **bloquear `moveToCourse` si el grupo tiene estudiantes matriculados** con `GroupHasStudentsException` (409), más simple que mantener la coherencia.
- **[Reescritura de `001` y `002`]** Cambios futuros que añadan tablas no pueden seguir el patrón incremental por unos días. → Mitigación: a partir de la próxima iteración vuelven a aplicarse las reglas del `AGENTS.md` (cambios incrementales).
- **[URLs breaking]** Consumidores externos (si los hubiera) pierden acceso. → Mitigación: el proyecto se mantiene en pre-producción, no hay clientes externos; documentado en `proposal.md`.
- **`ddl-auto=validate` puede bloquear el arranque** si los JPA entities no coinciden con el esquema Liquibase. → Mitigación: las nuevas entidades (`CenterJpaEntity`, `CourseJpaEntity`) y las columnas añadidas (`student.centerId`, `student_group.courseId`, `student_group_link.courseId`) se incluyen en el mismo PR que el changeset, con un `mvn compile` y un arranque manual de `mvn spring-boot:run` antes de cerrar la implementación.
- **[Detalle de grupo sin estudiantes]** `GET .../groups/{gid}/students/detail` debe funcionar con grupos vacíos. → Mitigación: el DTO de vista devuelve un array vacío en `students`, sin nulos.

## Plan de migración

No hay migración en el sentido tradicional (datos en producción). El flujo esperado al arrancar la aplicación tras el cambio:

1. Spring Boot levanta; Hibernate configura sus entities; Liquibase aplica `001` (esquema completo), `002` (seed) y `003` (columna `surname2`) sobre la base H2 recién creada.
2. Hibernate valida las entities contra el esquema (la operación es barata porque H2 se acaba de crear).
3. La aplicación queda lista con datos demo: 1 centro, 1 curso, 3 grupos, 4 estudiantes, 4 personas.

**Rollback:** no aplica en el sentido tradicional; basta con `git revert` o `git reset --hard` previo al commit. Como no hay estado persistente más allá del H2 local, no existe riesgo operacional.

## Open Questions

- *Borrado del grupo cuando tiene estudiantes.* El spec lo deja libre (dice "lo elimina"). La pregunta real es si el endpoint debe borrar en cascada las filas del junction o devolver 409 si hay estudiantes. Mi默认值 es **eliminar en cascada** para que `DELETE /groups/{id}` siga el contrato CRUD estándar; abriré esta cuestión al implementar si el usuario prefiere rechazo.
- *Nombre del Centro "IES Demo" en el seed.* El spec no lo fija; puedo proponer "IES Montecastelo" como temática coherente o "IES Demo" como neutro. Decisión menor, la dejo para cuando se ejecute la tarea de seed.
