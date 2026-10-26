# Propuesta

## Por qué

El modelo actual sólo distingue `Person`, `Student` y `Group`, con URLs en plano (`/api/v1/students`, `/api/v1/groups`) que no reflejan la jerarquía real de un centro educativo. Falta la noción de **Centro educativo** y de **Curso**, no hay forma de saber a qué centro pertenece un estudiante, y no existe la restricción "un estudiante puede estar en N grupos pero como mucho uno por curso". Esto se aborda ahora —antes de iterar con Profesores y Asignaturas— porque esos nuevos cambios presuponen la jerarquía Centro/Curso ya existente. Como la aplicación usa H2 in-memory recreada en cada arranque, los scripts de Liquibase pueden reescribirse sin riesgo sobre datos en producción.

## Qué cambia

- **Nuevos agregados `Center` y `Course`** con su CRUD completo y value objects `CenterId`, `CenterName`, `CourseId`, `CourseName`.
- **Reorganización de URLs** anidándolas bajo Centro y Curso. **BREAKING**:
  - `GET|POST /api/v1/students` → `GET|POST /api/v1/centers/{centerId}/students`
  - `GET|POST|PUT|DELETE /api/v1/students/{id}` → rutas equivalentes bajo `/api/v1/centers/{centerId}/students/{studentId}`
  - `GET|POST /api/v1/groups` → `GET|POST /api/v1/centers/{centerId}/courses/{courseId}/groups`
  - `GET|PUT|DELETE /api/v1/groups/{id}` → rutas equivalentes bajo `/centers/{cid}/courses/{coid}/groups/{gid}`
- **`Student` gana `centerId` mutable** vía `Student.changeCenter(CenterId)`, lo que formaliza la pertenencia del estudiante al centro.
- **`Group` gana `courseId` mutable** vía `Group.moveToCourse(CourseId)`, lo que formaliza la pertenencia del grupo al curso.
- **Nueva invariante**: `Student` puede estar matriculado en N grupos pero **como mucho uno por curso**. Se enforce en dos capas:
  - En el dominio: `Student.attachToGroup(Group)` rechaza si ya existe un `GroupId` cuyo `courseId()` coincide, lanzando `StudentAlreadyEnrolledInCourseException` (mapeada a `409 Conflict`).
  - En BD: índice único `UNIQUE (student_id, course_id)` sobre `student_group_link` (con `course_id` denormalizado). El adaptador rellena `course_id` al enlazar.
- **Operaciones de matrícula/desmatrícula se mueven** a `POST|DELETE /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/students/{studentId}`. **BREAKING**: la ruta anterior `/students/{sid}/groups/{gid}` desaparece.
- **Dos endpoints de detalle nuevo** que ensamblan datos de varios agregados:
  - `GET /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/students/detail` — datos del grupo + su curso + los estudiantes matriculados con los datos de su persona asociada.
  - `GET /api/v1/centers/{centerId}/students/{studentId}/detail` — datos del estudiante + su centro + su persona asociada + cada grupo en el que está matriculado con los datos del curso.
- **Listado de estudiantes de un grupo**: `GET /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/students/`.
- **Liquibase se reescribe libre**: `001-create-schema.yaml` y `002-seed-data.yaml` se sustituyen por el esquema completo (con `course_id` denormalizado en `student_group_link` y FK nuevas) más datos demo coherentes (1 centro "IES Demo", 1 curso "2026-2027" con los 3 grupos existentes y los 4 estudiantes reubicados). `003-add-person-surname2.yaml` se mantiene. `ddl-auto=validate` no se toca.

Fuera de alcance (van en cambios propios):

- Profesores (prompt `yt_openspec_02.md`).
- Asignaturas (prompt `yt_openspec_03.md`).
- Autenticación, autorización, paginación, versionado de cabeceras.

## Capacidades

### Capacidades nuevas

- `centers`: CRUD completo del agregado `Center` (`name` requerido, único en alcance del sistema), accesible vía `/api/v1/centers`.
- `courses`: CRUD completo del agregado `Course`, anidado bajo `Center`. `name` único dentro del centro (`UNIQUE(center_id, name)` en BD).
- `groups`: CRUD del agregado `Group` ahora anidado bajo `Center/Course`, más las operaciones de matrícula/desmatrícula del estudiante en el grupo, más el listado de estudiantes de un grupo y el endpoint de detalle del grupo. Incluye la nueva invariante "como mucho un grupo por curso" enforced en dominio y BD.
- `students`: CRUD del agregado `Student` ahora anidado bajo `Center`, más el endpoint de detalle que ensambla Centro + Persona + Grupos + Cursos del estudiante. `Student` pasa a tener `centerId` mutable.

### Capacidades modificadas

_Ninguna. El proyecto aún no tiene specs materializadas (`openspec list --specs` devuelve vacío), así que toda especificación de comportamiento es nueva._

## Impacto

- **Código Java**: `src/main/java/com/example/opencodetutorial/`
  - `domain/model/` — nuevos: `Center`, `CenterId`, `CenterName`, `Course`, `CourseId`, `CourseName`. Modificados: `Student` (añade `centerId` + `changeCenter` y reformula `attachToGroup(Group)` para chequear la invariante de unicidad por curso), `Group` (añade `courseId` + `moveToCourse`).
  - `domain/port/` — nuevos: `CenterRepository`, `CourseRepository`.
  - `domain/exception/` — nuevos: `CenterNotFoundException`, `CourseNotFoundException`, `StudentAlreadyEnrolledInCourseException`.
  - `application/usecase/` — nuevos casos de uso por agregado (create/list/get/update/delete) más `EnrollStudentInGroupUseCase`/`DetachStudentFromGroupUseCase` con la firma actualizada para trabajar con `Group` (no sólo `GroupId`). Casos de uso de detalle ensamblado (`GetGroupDetailUseCase`, `GetStudentDetailUseCase`).
  - `infrastructure/adapter/output/persistence/` — nuevas entidades JPA y adaptadores para `Center` y `Course`; `StudentJpaEntity` gana `centerId`; `GroupJpaEntity` gana `courseId`; `StudentJpaEntity` gana `Set<Long> courseIds` denormalizado para materializar el índice único compuesto, o se introduce una entidad de enlace explícita si la denormalización no encaja bien.
  - `infrastructure/adapter/input/rest/` — nuevos `CenterController`, `CourseController`; `GroupController` y `StudentController` se mueven a las rutas jerárquicas; nuevos DTOs de vista (`GroupDetailResponse`, `StudentDetailResponse`, `EnrolledStudentResponse`); `ApiExceptionHandler` añade mapeos para las nuevas `*NotFoundException` (`404`) y para `StudentAlreadyEnrolledInCourseException`/`DataIntegrityViolationException` (`409`).
- **Liquibase**: `src/main/resources/db/changelog/001-create-schema.yaml` reescrito; `002-seed-data.yaml` reescrito; `003-add-person-surname2.yaml` intacto; `db.changelog-master.yaml` intacto. `spring.jpa.hibernate.ddl-auto=validate` se mantiene.
- **Configuración / arranque**: ninguno. La fuente de datos H2 in-memory se recrea en cada boot, así que la reescritura de los scripts no requiere limpieza manual.
