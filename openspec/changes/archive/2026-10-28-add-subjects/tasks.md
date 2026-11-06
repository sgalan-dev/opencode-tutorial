# Tasks

## 1. Esquema Liquibase (changesets incrementales)

- [x] 1.1 Crear `src/main/resources/db/changelog/006-create-subjects.yaml` con el changeset `012-create-subject`: tabla `subject` (`id` autoincremental, `course_id` not null, `name` varchar(150) not null, `created_at`, `updated_at`), `UNIQUE (course_id, name)` con nombre `uk_subject_course_name` y FK `fk_subject_course` a `course(id)` con `ON DELETE RESTRICT`; verificación: `mvn spring-boot:run` aplica el changeset sin errores y la tabla existe en la consola H2.
- [x] 1.2 En el mismo fichero, añadir el changeset `013-create-subject-teaching`: tabla `subject_teaching` (`course_id` not null, `subject_id` not null, `professor_id` not null), `PRIMARY KEY (subject_id)` —un profesor como máximo por asignatura—, FK `fk_teaching_course` a `course(id)` `ON DELETE RESTRICT`, `fk_teaching_subject` a `subject(id)` `ON DELETE CASCADE` y `fk_teaching_professor` a `professor(id)` `ON DELETE RESTRICT`; verificación: el arranque completa sin `SchemaManagementException` y una segunda fila con el mismo `subject_id` es rechazada por la base de datos.
- [x] 1.3 Crear `src/main/resources/db/changelog/007-seed-subjects.yaml` con los datos de ejemplo del diseño: el curso 3 "Tercero Primaria" en el centro 2, cuatro asignaturas ("Matemáticas" y "Lengua" en el curso 1, "Matemáticas" en el curso 2, "Ciencias" en el curso 3) y dos filas de impartición (profesor 1 para la asignatura 1, profesor 2 para la asignatura 4); verificación: tras arrancar, `GET /api/v1/centers/1/courses/1/subjects` devuelve 2 asignaturas, una con `professorId` informado y otra sin él.
- [x] 1.4 Incluir `006-create-subjects.yaml` y `007-seed-subjects.yaml` en `db.changelog-master.yaml` sin tocar `001`–`005`; verificación: el log de arranque muestra los 7 ficheros liquibase aplicados en orden.

## 2. Capa de dominio (modelo, puertos y excepciones)

- [x] 2.1 Crear `domain/model/SubjectId` (record envoltorio de `Long`, valor positivo) y `domain/model/SubjectName` (record que recorta y rechaza valores en blanco o de más de 150 caracteres), sin imports de Spring, JPA, Jackson ni validación; verificación: `mvn compile` y el fichero no contiene imports prohibidos.
- [x] 2.2 Crear el agregado `domain/model/Subject` con `create(SubjectName, CourseId)`, `rehydrate(...)`, `rename(SubjectName)`, `assignProfessor(Professor, Course)` (rechaza con `SubjectProfessorInDifferentCenterException` si el centro del curso no es el del profesor, y reemplaza al profesor anterior) y `unassignProfessor()`, con `professorId` anulable y sin imports prohibidos; verificación: `mvn compile` y la firma de `assignProfessor` recibe el `Course` completo.
- [x] 2.3 Crear el puerto `domain/port/SubjectRepository` con `findById`, `findAll`, `findByCourseId`, `findByProfessorId`, `save`, `deleteById` y `existsById`; verificación: `mvn compile` y el puerto sólo depende de tipos del dominio.
- [x] 2.4 Crear `domain/exception/SubjectNotFoundException` y `domain/exception/SubjectProfessorInDifferentCenterException` con mensajes útiles; verificación: `mvn compile` y las dos extienden `RuntimeException`.

## 3. Adaptadores de persistencia

- [x] 3.1 Crear `SubjectJpaEntity` (tabla `subject`, `courseId` como columna escalar), `SubjectJpaRepository`, `SubjectTeachingJpaEntity` (tabla `subject_teaching`) y `SubjectTeachingJpaRepository`; verificación: `mvn spring-boot:run` arranca sin `SchemaManagementException`.
- [x] 3.2 Crear `SubjectPersistenceAdapter` implementando `SubjectRepository`, con el mapeo en ambos sentidos, escribiendo la fila de `subject_teaching` cuando cambia el profesor y borrándola cuando se desasigna, rehidratando el `professorId` desde la fila de impartición y cargando de una vez todas las imparticiones del curso en el listado; verificación: asignar y desasignar un profesor y releer la asignatura por `GET /api/v1/centers/1/courses/1/subjects/{id}` devuelve el valor persistido, y el listado no hace una consulta por asignatura.
- [x] 3.3 Traducir en el adaptador la violación de `uk_subject_course_name` a `DuplicateNameException` (409); verificación: crear dos asignaturas con el mismo nombre en el mismo curso responde 409 y el mismo nombre en otro curso responde 201.

## 4. Casos de uso de aplicación

- [x] 4.1 Añadir a `HierarchyQueries` el método `requireSubject(CenterId, CourseId, SubjectId)`, que exige centro existente, curso existente y del centro, y asignatura existente y del curso (404 en caso contrario); verificación: un `GET` de una asignatura de otro curso bajo este curso responde 404.
- [x] 4.2 Crear los comandos `CreateSubjectCommand`, `UpdateSubjectCommand`, `AssignProfessorCommand` y `UnassignProfessorCommand`; verificación: `mvn compile`.
- [x] 4.3 Implementar `CreateSubjectService` y `UpdateSubjectService`: validan la cadena centro → curso (404), el nombre (400) y el nombre duplicado dentro del curso (409); verificación: crear dos asignaturas con el mismo nombre en el mismo curso responde 409 y en otro curso responde 201, y un `PUT` sobre una asignatura con profesor lo conserva.
- [x] 4.4 Implementar `ListSubjectsService`, `GetSubjectService` y `DeleteSubjectService`; el listado devuelve sólo las asignaturas del curso de la URL; verificación: el listado de un curso devuelve únicamente sus asignaturas y borrar una asignatura de otro curso responde 404.
- [x] 4.5 Implementar `AssignProfessorToSubjectService` y `UnassignProfessorFromSubjectService`: resuelven la cadena centro → curso → asignatura, exigen que el profesor pertenezca al centro (404) y aplican `assignProfessor`/`unassignProfessor` sobre el agregado, que rechaza con 409 al profesor de otro centro; verificación: asignar el profesor que ya tiene responde 204 sin cambios y desasignar un profesor que no es el actual responde 204 sin cambios.
- [x] 4.6 Modificar `DeleteCourseService` para contar también las asignaturas dependientes (409) y `DeleteProfessorService` para contar las asignaturas que imparte (409), con el patrón `ResourceHasDependentsException`; verificación: `DELETE` de un curso con asignaturas responde 409 y el de un profesor que imparte responde 409.

## 5. Capa REST (controlador, DTOs, mapper y handler)

- [x] 5.1 Crear `dto/SubjectRequest` (`name` con `@NotBlank`), `dto/SubjectResponse` (`id`, `courseId`, `name`, `professorId` anulable) y `mapper/SubjectRequestMapper`; verificación: `mvn compile` y la respuesta reporta el profesor como ausente cuando la asignatura no lo tiene.
- [x] 5.2 Crear `SubjectController` bajo `/api/v1/centers/{centerId}/courses/{courseId}/subjects` con `GET` y `POST` de la colección y `GET|PUT|DELETE` del elemento, cada uno anotado con `@Operation`, `@ApiResponse` y `@Tag`; verificación: `GET /api/v1/centers/1/courses/1/subjects` responde 200 y `POST` responde 201 con cabecera `Location`.
- [x] 5.3 Añadir a `SubjectController` los endpoints `POST` y `DELETE .../subjects/{subjectId}/professor/{professorId}`, ambos idempotentes y con respuesta 204; verificación: `POST .../subjects/1/professor/1` responde 204 y `GET .../subjects/1` devuelve `professorId` = 1.
- [x] 5.4 Registrar en `ApiExceptionHandler` los nuevos mapeos: `SubjectNotFoundException` → 404, `SubjectProfessorInDifferentCenterException` → 409 y el `DataIntegrityViolationException` que mencione `UK_SUBJECT_COURSE_NAME` → 409; verificación: cada excepción produce el código y el cuerpo de error esperados (`timestamp`/`status`/`error`/`message`).

## 6. Verificación de integración

- [x] 6.1 `mvn -q -DskipTests compile` finaliza con BUILD SUCCESS; verificación: salida del comando sin errores.
- [x] 6.2 `mvn -q test` finaliza sin fallos; verificación: salida del comando sin errores (el árbol de tests sigue vacío, en línea con las iteraciones anteriores).
- [x] 6.3 `mvn -q spring-boot:run` arranca con el log limpio, sin `SchemaManagementException` ni `BeanCreationException`; verificación: la aplicación responde en `/api/v1/centers`.
- [x] 6.4 Smoke manual de las reglas nuevas contra la aplicación arrancada:
  - `POST /api/v1/centers/{c}/courses/{courseId}/subjects` con `name` válido → 201 con `Location`.
  - Repetirlo con el mismo nombre en el mismo curso → 409; con el mismo nombre en otro curso → 201.
  - `GET /api/v1/centers/{c}/courses/{courseId}/subjects` → 200 sólo con las asignaturas de ese curso.
  - `POST .../subjects/{s}/professor/{p}` con un profesor de otro centro → 404 y la asignatura conserva su profesor.
  - `POST .../subjects/{s}/professor/{p2}` → 204 y el profesor anterior queda reemplazado.
  - `DELETE .../subjects/{s}/professor/{p2}` → 204 y la asignatura queda sin profesor.
  - `PUT .../subjects/{s}` → 200 y la asignatura conserva su profesor.
  - `DELETE /api/v1/centers/{c}/courses/{courseId}` con asignaturas → 409.
  - `DELETE /api/v1/centers/{c}/professors/{p}` con un profesor que imparte → 409.
- [x] 6.5 Revisar `/swagger-ui.html` y `/v3/api-docs`: los 7 endpoints nuevos aparecen documentados y la respuesta de asignatura muestra el profesor; verificación: captura del documento OpenAPI con los endpoints nuevos y el campo `professorId`.

## Workflow follow-up

- Archivar el cambio con `/opsx-archive add-subjects` cuando todas las tareas estén marcadas.
- Verificar que el archivado crea `openspec/specs/subjects/spec.md` y actualiza `openspec/specs/courses/spec.md` y `openspec/specs/professors/spec.md` con los requisitos modificados y añadidos.
- Ajustar en los specs materializados el `Purpose` de `courses` y `professors` para mencionar las asignaturas, tal como queda apuntado en las preguntas abiertas del diseño.
