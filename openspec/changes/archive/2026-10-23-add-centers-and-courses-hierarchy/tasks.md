# Tasks

## 1. Esquema Liquibase y entidades JPA

- [x] 1.1 Reescribir `src/main/resources/db/changelog/001-create-schema.yaml` con el esquema completo (Centros, Cursos, StudentGroup con `course_id`, Student con `center_id`, `student_group_link` con `course_id` denormalizado y `UNIQUE(student_id, course_id)`); verificación: `mvn compile` y arranque manual sin excepciones de Liquibase.
- [x] 1.2 Reescribir `src/main/resources/db/changelog/002-seed-data.yaml` con 1 Centro ("IES Demo"), 1 Curso ("2026-2027"), los 3 grupos originales y los 4 estudiantes reubicados; verificación: tras arrancar, `GET /api/v1/centers`, `GET /api/v1/centers/1/courses`, `GET /api/v1/centers/1/courses/1/groups` y `GET /api/v1/centers/1/students` devuelven datos demo coherentes.
- [x] 1.3 Añadir `center_id` a `StudentJpaEntity` y `course_id` a `GroupJpaEntity`, y exponer la denormalización del enlace en el `@ElementCollection` o en una nueva entidad de enlace; verificación: Hibernate valida el esquema al arrancar (`ddl-auto=validate` no lanza).
- [x] 1.4 Crear `CenterJpaEntity` y `CourseJpaEntity` con sus repositorios JPA (`CenterJpaRepository`, `CourseJpaRepository`); verificación: arrancada limpia sin `SchemaManagementException`.

## 2. Capa de dominio (modelo + puertos + excepciones)

- [x] 2.1 Crear `CenterId` (record envoltorio de `Long`) y `CenterName` (record con constructor compacto que enforce no-blank, longitud ≤ 150); verificación: tests manuales o `mvn test` si existen, o código compilable que rechaza nombres vacíos.
- [x] 2.2 Crear agregado `Center` con `create(CenterName)`, `rehydrate(...)`, `rename(CenterName)`; verificación: invariantes pasan las pruebas de compilación y el agregado es independiente de frameworks (sin imports de Spring/JPA).
- [x] 2.3 Crear `CourseId`, `CourseName` (longitud ≤ 150) y agregado `Course` con `create(CourseName, CenterId)`, `rehydrate(...)`, `rename(CourseName)`, `moveToCenter(CenterId)`; verificación: misma independencia que `Center`.
- [x] 2.4 Crear puertos `CenterRepository` y `CourseRepository` con `findById`, `findAll`, `findByCenterId` (sólo en CourseRepository), `save`, `deleteById`, `existsById`; verificación: compilan y dependen sólo de tipos de dominio.
- [x] 2.5 Modificar `Group`: añadir `courseId` mutable vía `moveToCourse(CourseId)`. Si el agregado tiene estudiantes matriculados, `moveToCourse` lanza `GroupHasStudentsException` → 409; verificación: firma compila y método mantiene coherencia de denormalización en la persistencia.
- [x] 2.6 Modificar `Student`: añadir `centerId` mutable vía `changeCenter(CenterId)`. Cambiar `attachToGroup(GroupId)` por `attachToGroup(Group)` que:
  - añade el `groupId()` si no estaba;
  - **rechaza si ya existe en `groupIds` un grupo cuyo `courseId()` coincide** con `group.courseId()`, lanzando `StudentAlreadyEnrolledInCourseException`;
  - rechaza si `group.courseId()` pertenece a un curso de otro centro que el `student.centerId` (`StudentInDifferentCenterException` → 409).
  Mantener `replaceGroups(Set<GroupId>)` y `detachFromGroup` como están. Verificación: el agregado compila y los nuevos métodos son accesibles desde los servicios.
- [x] 2.7 Crear `CenterNotFoundException`, `CourseNotFoundException`, `StudentAlreadyEnrolledInCourseException`, `GroupHasStudentsException`; verificación: extienden `RuntimeException` con mensajes útiles.

## 3. Adaptadores de persistencia

- [x] 3.1 `CenterPersistenceAdapter` (implementa `CenterRepository`) y `CenterJpaRepository` (`extends JpaRepository<CenterJpaEntity, Long>`); verificación: la app arranca e `GET /api/v1/centers` devuelve los centros sembrados.
- [x] 3.2 `CoursePersistenceAdapter` con `findByCenterId(CenterId)` que filtra en JPA, y `CourseJpaRepository` con `findByCenterId(Long)`; verificación: `GET /api/v1/centers/1/courses` devuelve sólo cursos del centro 1.
- [x] 3.3 Actualizar `GroupPersistenceAdapter` para mapear `Group.courseId()` <-> `GroupJpaEntity.getCourseId()`; verificación: `GET /api/v1/centers/1/courses/1/groups` devuelve los 3 grupos demo.
- [x] 3.4 Actualizar `StudentPersistenceAdapter` para mapear `Student.centerId()` y rellenar `course_id` denormalizado en cada fila del junction. Capturar `DataIntegrityViolationException` cuya causa sea violación de `uk_student_course` y relanzar como `StudentAlreadyEnrolledInCourseException`; verificación: intentar insertar dos filas con el mismo `(student_id, course_id)` directamente vía SQL de prueba devuelve 409 con el mismo mensaje que la capa de dominio.

## 4. Casos de uso de aplicación

- [x] 4.1 `CreateCenterUseCase`, `ListCentersUseCase`, `GetCenterUseCase`, `UpdateCenterUseCase`, `DeleteCenterUseCase` con sus `command` records; verificación: las clases compilan y siguen las convenciones `@Service` + `@Transactional` en el boundary.
- [x] 4.2 Casos de uso de Curso equivalentes más los comandos (`CreateCourseCommand` lleva `centerId`, `UpdateCourseCommand` también); verificación: `CreateCourseService` rechaza con `CenterNotFoundException` cuando el centro no existe.
- [x] 4.3 Actualizar `CreateGroupService`, `UpdateGroupService`, `GetGroupService`, `ListGroupsService`, `DeleteGroupService` para que exijan el padre (centro → curso) en la URL canónica; `DeleteGroupService` borra en cascada las filas del junction (lo exige el spec `groups`). Renombrar `AssignStudentToGroupService` ↔ `EnrollStudentInGroupService` y `DetachStudentFromGroupService` para reflejar las nuevas URLs; pasar a ambos el `Group` resuelto y el `Student` cargado, y añadir las validaciones de mismo centro y regla de unicidad por curso.
- [x] 4.4 Casos de uso de Estudiante (`CreateStudentService`, etc.) que exijan el `centerId` en la URL y verifiquen que cada `groupId` pertenece al mismo centro.
- [x] 4.5 `ListStudentsInGroupService`: devuelve los estudiantes matriculados en un grupo (obtenidos vía `StudentRepository.findAll` filtrados en memoria por `groupId`, dado el tamaño esperado del dominio; alternativa con JPA documentada en la firma si crece).
- [x] 4.6 `GetGroupDetailUseCase`: orquesta `GroupRepository.findById`, `CourseRepository.findById`, `StudentRepository.findAll` (filtrado por groupIds) y `PersonRepository.findById` por cada estudiante. Devuelve `GroupDetailView`.
- [x] 4.7 `GetStudentDetailUseCase`: orquesta `StudentRepository.findById`, `CenterRepository.findById(student.centerId)`, `PersonRepository.findById(student.personId)`, `GroupRepository.findAll` (filtrado por `student.groupIds`), y por cada grupo, `CourseRepository.findById(group.courseId)`. Devuelve `StudentDetailView`.

## 5. Capa REST (controladores + DTOs + mappers + handler)

- [x] 5.1 DTOs de request/response para Centro (`CenterRequest` con `@NotBlank @Size(max=150) String name`, `CenterResponse`) y mappers asociados; verificación: validación de Bean activa (`@Valid` en el controlador).
- [x] 5.2 DTOs y mappers análogos para Curso (`CourseRequest`, `CourseResponse` con `centerId`).
- [x] 5.3 Reubicar `GroupController` bajo `/api/v1/centers/{centerId}/courses/{courseId}/groups` (sigue exponiendo los mismos verbos) y actualizar `GroupResponse` para incluir `courseId`. DTOs `GroupDetailResponse` con campos `group`, `course`, `students[]` donde cada estudiante incluye `id`, `centerId`, `personId` y un sub-objeto `person` con `name`, `surname`, `surname2`. Verificación: `GET .../groups/{gid}/students/detail` responde 200 con la estructura esperada.
- [x] 5.4 Reubicar `StudentController` bajo `/api/v1/centers/{centerId}/students`. Actualizar `StudentResponse` para incluir `centerId`. DTO `StudentDetailResponse` con campos `student`, `center`, `person` y `enrolments[]` donde cada elemento lleva `group` y `course`. Verificación: `GET .../students/{sid}/detail` responde 200 con la estructura esperada.
- [x] 5.5 Mover las operaciones de matrícula/desmatrícula a `GroupController` con `POST|DELETE /api/v1/centers/{centerId}/courses/{courseId}/groups/{groupId}/students/{studentId}`. Verificación: la ruta antigua `/api/v1/students/{sid}/groups/{gid}` deja de existir (404) y la nueva responde 204.
- [x] 5.6 Actualizar `ApiExceptionHandler` con handlers para `CenterNotFoundException`, `CourseNotFoundException`, `GroupHasStudentsException`, `StudentAlreadyEnrolledInCourseException`, `DataIntegrityViolationException` (cuando aplique a `uk_student_course`), `StudentInDifferentCenterException`. Verificación: cada uno devuelve el código correcto (`404` o `409`) con cuerpo de error consistente (`timestamp/status/error/message`).
- [x] 5.7 Persona mantiene su controlador en raíz (`/api/v1/persons`); ningún cambio. Verificación: los endpoints existentes siguen respondiendo.
- [x] 5.8 Verificación manual final de OpenAPI: `mvn spring-boot:run`, abrir `/swagger-ui.html` y `/v3/api-docs`, comprobar que cada nuevo endpoint aparece, que cada antigua URL plana ya no aparece, que las anotaciones `@Operation`, `@ApiResponse`, `@Tag` están actualizadas con los nuevos paths y descripciones.

## 6. Verificación de integración

- [x] 6.1 `mvn -q -DskipTests compile` finaliza sin errores; verificación: BUILD SUCCESS sin warnings críticos.
- [x] 6.2 `mvn -q test` finaliza con los tests existentes pasando (no se introducen nuevos tests en esta iteración salvo que se decida lo contrario; ver `AGENTS.md`).
- [x] 6.3 `mvn -q spring-boot:run` arranca; `GET /actuator/health` (si existe) o el primer endpoint responde 200; `GET /h2-console` carga (manual). Verificación: log de arranque limpio, sin `SchemaManagementException`, sin `BeanCreationException`.
- [x] 6.4 Smoke manual de las invariantes críticas:
  - POST `/api/v1/centers` con nombre nuevo → 201.
  - POST `/api/v1/centers/{c}/courses` → 201; con nombre repetido en el mismo centro → 409.
  - POST `/api/v1/centers/{c}/courses/{co}/groups` → 201.
  - POST `/api/v1/centers/{c}/students` con `personId` y sin grupos → 201.
  - POST `.../groups/{g}/students/{s}` (matricular) → 204.
  - Repetir el POST anterior → 204 idempotente.
  - Intentar matricular al mismo estudiante en otro grupo del mismo curso → 409 con mensaje "ya está matriculado en un grupo de este curso".
  - Matricular al estudiante en otro grupo de otro curso (si hay otro curso) → 204.
  - GET `.../students/{s}/detail` → 200 con la estructura ensamblada.
  - GET `.../groups/{g}/students/detail` → 200 con la lista de estudiantes y sus personas.

## Workflow follow-up

- Archivar el cambio con `/opsx-archive add-centers-and-courses-hierarchy` cuando todas las tareas estén marcadas.
- Verificar que el archive copia los specs a `openspec/specs/centers`, `openspec/specs/courses`, `openspec/specs/groups`, `openspec/specs/students` con el contenido correcto.
