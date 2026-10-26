# Propuesta

## Por qué

El centro educativo no tiene ninguna entidad que represente al personal docente: la jerarquía actual es `Centro → Curso → Grupo → Estudiante` y no hay forma de registrar quién imparte un grupo ni de asignar un tutor responsable. La figura del profesor es además el único actor con responsabilidades sobre un grupo, y es el eslabón que necesitarán las asignaturas en iteraciones posteriores (`docs/prompts/yt_openspec_03.md`). Este cambio introduce `Professor` como la asociación entre una persona y un centro concreto, y el tutor como una propiedad del grupo.

## Qué cambia

- **Nuevo agregado `Professor`**: la asociación de una persona con un centro educativo. La persona es global (no cambia de una fila de profesor a otra), pero el profesor es por centro: una misma persona puede ser profesor en varios centros y nunca duplicada dentro del mismo centro.
- **CRUD completo de profesores, anidado bajo el centro**, siguiendo la convención de URL del proyecto:
  - `GET|POST /api/v1/centers/{centerId}/professors`
  - `GET|PUT|DELETE /api/v1/centers/{centerId}/professors/{professorId}`
  - `GET /api/v1/centers/{centerId}/professors/{professorId}/detail` — el profesor, su centro y los datos personales de la persona asociada (equivalente al detalle del estudiante, sin el listado de grupos tutelados).
- **Un grupo puede tener un tutor**, que es un profesor del centro al que pertenece el grupo (el centro del grupo se deduce de su curso).
- **Endpoints dedicados para asignar y desasignar el tutor**:
  - `POST /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/tutor/{professorId}`
  - `DELETE /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/tutor/{professorId}`
  Asignar un tutor **reemplaza** al que tuviera el grupo. Ambas operaciones son idempotentes y responden 204. El `PUT` del grupo sigue siendo un cambio de nombre y no toca el tutor.
- **El tutor pasa a ser visible en las consultas de grupo**: `tutorProfessorId` en el recurso de grupo y un bloque `tutor` (profesor + datos de su persona) en el detalle del grupo.
- **Esquema Liquibase incremental**: nueva tabla `professor` y nueva columna `student_group.tutor_professor_id` (anulable, con clave foránea a `professor`). Se añaden dos changesets nuevos; no se reescriben `001`, `002` ni `003`.
- **Borrados bloqueados**: un profesor no se puede borrar mientras tutorice algún grupo (409) y un centro no se puede borrar mientras tenga profesores (409).
- **Datos de ejemplo**: un segundo centro, profesores repartidos entre centros (incluida una persona que es profesor en dos centros a la vez) y grupos con y sin tutor.

Fuera de alcance (van en cambios propios):

- Materias y asignaturas del profesor (`docs/prompts/yt_openspec_03.md`).
- Descubrimiento inverso persona → centros donde es profesor (`GET /api/v1/persons/{personId}/professors`): no está en la jerarquía de URLs pactada y no se expone en esta iteración.
- Atributos del profesor más allá de persona y centro (departamento, materia, código de empleado).
- Autenticación, autorización, paginación, borrado lógico.

## Capacidades

### Capacidades nuevas

- `professors`: CRUD completo del agregado `Professor` anidado bajo `Center`, con la unicidad `(person_id, center_id)` en dos capas (dominio + base de datos), el endpoint de detalle que ensambla profesor + centro + persona, y la prohibición de borrar un profesor que tuteliza grupos.

### Capacidades modificadas

- `groups`: el grupo gana un tutor opcional; se añaden las operaciones de asignar/desasignar tutor y la invariante "el tutor es un profesor del centro del grupo"; las respuestas de grupo exponen el tutor.
- `centers`: el borrado de un centro con profesores dependientes pasa a rechazarse con 409, igual que ya ocurre con cursos y estudiantes.

## Impacto

- **Código Java** (`src/main/java/com/example/opencodetutorial/`):
  - `domain/model/` — nuevos: `Professor`, `ProfessorId`. Modificado: `Group` (añade `tutorProfessorId` anulable, `assignTutor(Professor, Course)` y `unassignTutor()`).
  - `domain/port/` — nuevo: `ProfessorRepository` (`findById`, `findAll`, `findByCenterId`, `save`, `deleteById`, `existsById`). Modificado: `GroupRepository` (añade `findByTutorProfessorId`, usado sólo como guarda de borrado, no se expone en la API).
  - `domain/exception/` — nuevos: `ProfessorNotFoundException`, `DuplicateProfessorInCenterException`, `TutorInDifferentCenterException`.
  - `application/usecase/` — nuevos casos de uso de profesor (create/list/get/update/delete/detail) más `AssignTutorToGroupUseCase` y `UnassignTutorFromGroupUseCase`, con sus `command` records; nueva vista `ProfessorDetailView`. Modificados: `DeleteCenterService` (comprueba también los profesores dependientes) y `GetGroupDetailService` (carga el tutor y su persona).
  - `infrastructure/adapter/output/persistence/` — nuevos: `ProfessorJpaEntity`, `ProfessorJpaRepository`, `ProfessorPersistenceAdapter`. Modificados: `GroupJpaEntity` (columna `tutorProfessorId`) y `GroupPersistenceAdapter` (mapeo en ambos sentidos).
  - `infrastructure/adapter/input/rest/` — nuevos: `ProfessorController`, `dto/ProfessorRequest`, `dto/ProfessorResponse`, `dto/ProfessorDetailResponse`, `dto/TutorResponse`, `mapper/ProfessorRequestMapper`. Modificados: `GroupController` (nuevos endpoints de tutor), `GroupResponse` (añade `tutorProfessorId`), `GroupDetailResponse` (añade `tutor`), `PersonSummaryResponse` (su descripción deja de mencionar sólo al estudiante), `ApiExceptionHandler` (nuevos mapeos 404/409).
- **Liquibase** (`src/main/resources/db/changelog/`): nuevos `004-create-professor.yaml` y `005-seed-professors.yaml`, incluidos desde `db.changelog-master.yaml`. `001`, `002` y `003` intactos. `spring.jpa.hibernate.ddl-auto=validate` no se toca.
- **API**: siete endpoints nuevos y dos respuestas existentes ampliadas (`GroupResponse`, `GroupDetailResponse`) con campos nuevos. No se elimina ni renombra ninguna ruta existente, por lo que no hay cambios incompatibles.
