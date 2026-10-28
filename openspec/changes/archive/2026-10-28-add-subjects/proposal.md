# Propuesta

## Por qué

El centro educativo modela hoy `Centro → Curso → Grupo → Estudiante` y el claustro (`Professor`), pero no tiene ninguna entidad que represente **qué se imparte** en cada curso. Una asignatura es hoy invisible: no se puede registrar que un curso da "Matemáticas", ni quién es el especialista que la imparte, ni dejar constancia de que esa asignatura todavía no tiene profesor. La asimetría con el grupo es reveladora: el grupo tiene su tutor (el responsable), pero la asignatura —el contenido real del curso— no tiene a su profesor (el especialista). Este cambio introduce `Subject` como hija del curso y el mecanismo que relaciona curso, asignatura y profesor que la imparte.

## Qué cambia

- **Nuevo agregado `Subject`**: una asignatura pertenece exactamente a un curso y tiene un nombre. Se modela como agregado raíz con identificador propio (`SubjectId`), igual que `Course` o `Group`, aunque sus URLs cuelguen del curso que la posee.
- **CRUD completo de asignaturas, anidado bajo el curso**:
  - `GET|POST /api/v1/centers/{centerId}/courses/{courseId}/subjects`
  - `GET|PUT|DELETE /api/v1/centers/{centerId}/courses/{courseId}/subjects/{subjectId}`
- **Una asignatura puede tener un profesor asignado**, que es el especialista que la imparte en ese curso. La asignación es opcional: una asignatura sin profesor es un estado legítimo.
- **Endpoints dedicados para asignar y desasignar el profesor**:
  - `POST /api/v1/centers/{centerId}/courses/{courseId}/subjects/{subjectId}/professor/{professorId}`
  - `DELETE /api/v1/centers/{centerId}/courses/{courseId}/subjects/{subjectId}/professor/{professorId}`
  Asignar **reemplaza** al profesor anterior. Ambas operaciones son idempotentes y responden 204. El `PUT` de la asignatura sigue siendo un cambio de nombre y **no** toca el profesor, exactamente igual que el `PUT` del grupo con su tutor.
- **Tabla de impartición**: la relación curso ↔ asignatura ↔ profesor se materializa en una tabla propia (`subject_teaching`), no como una columna de la asignatura. El `course_id` va desnormalizado en ella, igual que en `student_group_link`.
- **La respuesta de asignatura expone su profesor** (`professorId`) y lo reporta como ausente cuando la asignatura todavía no tiene ninguno.
- **Esquema Liquibase incremental**: nueva tabla `subject` y nueva tabla `subject_teaching`, con sus claves foráneas. Se añaden changesets nuevos; no se reescriben `001`–`005`.
- **Borrados bloqueados**: un curso no se puede borrar mientras tenga asignaturas (409) y un profesor no se puede borrar mientras imparta alguna asignatura (409).
- **Reglas de integridad**: el nombre de la asignatura es único dentro del curso (409 si se repite) y el profesor que imparte tiene que ser profesor del centro al que pertenece el curso (409).
- **Datos de ejemplo**: asignaturas con profesor y sin profesor, el mismo nombre de asignatura en dos cursos distintos, y un curso en un segundo centro con su propio profesor.

Fuera de alcance (van en cambios propios):

- **La impartición por defecto a cargo del tutor del grupo** cuando la asignatura no tiene profesor: es un desarrollo futuro y no se resuelve en este cambio (`docs/prompts/yt_openspec_03.md`).
- El detalle compuesto de la asignatura (`.../subjects/{subjectId}/detail`): el prompt no lo pide y el recurso normal ya expone el `professorId`.
- La reorganización de la rama `Personas` de la jerarquía: ya está hecha y no se toca.
- El descubrimiento inverso "asignaturas que imparte un profesor" fuera de la jerarquía de URLs pactada.
- Autenticación, autorización, paginación y borrado lógico.

## Capacidades

### Capacidades nuevas

- `subjects`: CRUD del agregado `Subject` anidado bajo `Course`, con la unicidad del nombre dentro del curso, la asignación y desasignación del profesor que la imparte, la invariante "el profesor que imparte es del centro del curso", la exposición del profesor en la respuesta de la asignatura y la prohibición de borrar un curso con asignaturas o un profesor que imparte alguna.

### Capacidades modificadas

- `courses`: el borrado de un curso pasa a rechazarse con 409 también cuando tiene asignaturas dependientes, no sólo grupos.
- `professors`: el profesor pasa a tener una relación con las asignaturas que imparte, y su borrado se rechaza con 409 mientras imparta alguna.

## Impacto

- **Código Java** (`src/main/java/com/example/opencodetutorial/`):
  - `domain/model/` — nuevos: `Subject`, `SubjectId`, `SubjectName`. `Course` y `Group` no cambian.
  - `domain/port/` — nuevo: `SubjectRepository` (`findById`, `findAll`, `findByCourseId`, `findByProfessorId`, `save`, `deleteById`, `existsById`).
  - `domain/exception/` — nuevos: `SubjectNotFoundException`, `SubjectProfessorInDifferentCenterException`.
  - `application/usecase/` — nuevos casos de uso de asignatura (crear, listar, obtener, actualizar, borrar) más `AssignProfessorToSubjectUseCase` y `UnassignProfessorFromSubjectUseCase`, con sus `command` records; `HierarchyQueries` gana `requireSubject(...)`.
  - `infrastructure/adapter/output/persistence/` — nuevos: `SubjectJpaEntity`, `SubjectJpaRepository`, `SubjectPersistenceAdapter`, `SubjectTeachingJpaEntity`, `SubjectTeachingJpaRepository`.
  - `infrastructure/adapter/input/rest/` — nuevos: `SubjectController`, `dto/SubjectRequest`, `dto/SubjectResponse`, `mapper/SubjectRequestMapper`; `ApiExceptionHandler` con los nuevos mapeos 404/409.
- **Liquibase** (`src/main/resources/db/changelog/`): nuevos `006-create-subjects.yaml` y `007-seed-subjects.yaml`, incluidos desde `db.changelog-master.yaml`. `001`–`005` intactos. `spring.jpa.hibernate.ddl-auto=validate` no se toca.
- **API**: siete endpoints nuevos. No se elimina ni renombra ninguna ruta existente, por lo que no hay cambios incompatibles.
