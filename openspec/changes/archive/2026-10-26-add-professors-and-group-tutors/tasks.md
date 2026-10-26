# Tasks

## 1. Esquema Liquibase (changesets incrementales)

- [x] 1.1 Crear `src/main/resources/db/changelog/004-create-professor.yaml` con el changeset `009-create-professor`: tabla `professor` (`id` autoincremental, `person_id` not null, `center_id` not null, `created_at`, `updated_at`), `UNIQUE (person_id, center_id)` con nombre `uk_professor_person_center`, FK `fk_professor_person` a `person(id)` y `fk_professor_center` a `center(id)`, ambas `ON DELETE RESTRICT`; verificación: `mvn spring-boot:run` aplica el changeset sin errores y la tabla existe en la consola H2.
- [x] 1.2 En el mismo fichero, añadir el changeset `010-add-group-tutor-column` (columna `tutor_professor_id` bigint nullable en `student_group`) y el changeset `011-add-group-tutor-fk` (FK `fk_group_tutor_professor` a `professor(id)` con `ON DELETE RESTRICT`), en ese orden; verificación: el arranque completa sin `SchemaManagementException` y `student_group.tutor_professor_id` acepta `NULL`.
- [x] 1.3 Crear `src/main/resources/db/changelog/005-seed-professors.yaml` con los datos de ejemplo del diseño: centro 2 "IES Segundo centro", la persona 5 "Katherine Johnson", tres profesores (persona 4 @ centro 1, persona 4 @ centro 2, persona 5 @ centro 1) y la asignación de tutor a los grupos 1 y 3, dejando el grupo 2 sin tutor; verificación: tras arrancar, `GET /api/v1/centers` devuelve 2 centros y `GET /api/v1/centers/1/courses/1/groups` devuelve 3 grupos, dos de ellos con `tutorProfessorId` informado.
- [x] 1.4 Incluir `004-create-professor.yaml` y `005-seed-professors.yaml` en `db.changelog-master.yaml` sin tocar `001`, `002` ni `003`; verificación: el log de arranque muestra los 5 ficheros liquibase aplicados en orden.

## 2. Capa de dominio (modelo, puertos y excepciones)

- [x] 2.1 Crear `domain/model/ProfessorId` (record envoltorio de `Long`) y el agregado `domain/model/Professor` con `create(PersonId, CenterId)`, `rehydrate(...)` y `changePerson(PersonId)`, sin imports de Spring, JPA, Jackson ni validación; verificación: `mvn compile` y el fichero no contiene imports prohibidos.
- [x] 2.2 Modificar `domain/model/Group` para que lleve `tutorProfessorId` anulable, con los métodos `assignTutor(Professor tutor, Course course)` (rechaza si el centro del curso no es el del profesor, lanzando `TutorInDifferentCenterException`, y reemplaza cualquier tutor anterior) y `unassignTutor()`, más el accessor correspondiente; verificación: `mvn compile` y la firma de `assignTutor` recibe el `Course` completo.
- [x] 2.3 Crear el puerto `domain/port/ProfessorRepository` con `findById`, `findAll`, `findByCenterId`, `save`, `deleteById` y `existsById`, y añadir `findByTutorProfessorId(ProfessorId)` a `GroupRepository`; verificación: `mvn compile` y los puertos sólo dependen de tipos del dominio.
- [x] 2.4 Crear `domain/exception/ProfessorNotFoundException`, `DuplicateProfessorInCenterException` y `TutorInDifferentCenterException` con mensajes útiles; verificación: `mvn compile` y las tres extienden `RuntimeException`.

## 3. Adaptadores de persistencia

- [x] 3.1 Crear `ProfessorJpaEntity` (tabla `professor`, `personId` y `centerId` como columnas escalares), `ProfessorJpaRepository` (`extends JpaRepository<ProfessorJpaEntity, Long>`) y `ProfessorPersistenceAdapter` implementando `ProfessorRepository` con el mapeo en ambos sentidos; verificación: `mvn spring-boot:run` arranca sin `SchemaManagementException` y `GET /api/v1/centers/1/professors` devuelve los profesores sembrados.
- [x] 3.2 Añadir `tutorProfessorId` a `GroupJpaEntity` y mapearlo en `GroupPersistenceAdapter` (`toEntity` y `toDomain`); verificación: asignar y desasignar un tutor y releer el grupo por `GET /api/v1/centers/1/courses/1/groups/{id}` devuelve el valor persistido, y el `save` de un grupo rehidratado no borra el tutor.

## 4. Casos de uso de aplicación

- [x] 4.1 Añadir a `HierarchyQueries` el método `requireProfessor(CenterId, ProfessorId)`, que exige centro existente y profesor perteneciente a ese centro (404 en caso contrario, con el mismo criterio que `requireCourse` y `requireStudent`); verificación: un `GET` de un profesor de otro centro bajo un centro ajeno responde 404.
- [x] 4.2 Crear los comandos `CreateProfessorCommand`, `UpdateProfessorCommand`, `AssignTutorCommand` y `UnassignTutorCommand`; verificación: `mvn compile`.
- [x] 4.3 Implementar `CreateProfessorService` y `UpdateProfessorService`: validan que la persona exista (404), que el centro exista (404) y que la pareja `(person_id, center_id)` no esté ya ocupada (409 con `DuplicateProfessorInCenterException`); verificación: crear dos profesores con la misma persona en el mismo centro devuelve 409, y la misma persona en otro centro devuelve 201.
- [x] 4.4 Implementar `ListProfessorsService`, `GetProfessorService` y `DeleteProfessorService`; el borrado cuenta los grupos tutelados y lanza `ResourceHasDependentsException` (409) si hay alguno; verificación: `DELETE` de un profesor con grupos tutelados responde 409 y el de uno sin tutor responde 204.
- [x] 4.5 Implementar `GetProfessorDetailService` y la vista `application/usecase/view/ProfessorDetailView` (profesor + centro + persona, sin los grupos tutelados); verificación: `GET /api/v1/centers/1/professors/1/detail` responde 200 con las tres secciones.
- [x] 4.6 Implementar `AssignTutorToGroupService` y `UnassignTutorFromGroupService`: resuelven la cadena centro → curso → grupo, exigen que el profesor pertenezca al centro (404) y aplican `assignTutor`/`unassignTutor` sobre el agregado; verificación: asignar un tutor ya existente responde 204 sin cambios, desasignar un tutor que no es el actual responde 204 sin cambios.
- [x] 4.7 Modificar `GetGroupDetailService` para cargar el profesor tutor y la persona asociada cuando el grupo tenga tutor, y `DeleteCenterService` para rechazar con 409 los centros que tengan profesores; verificación: el detalle de un grupo con tutor incluye los datos de la persona del tutor y `DELETE /api/v1/centers/2` (con profesores) responde 409.

## 5. Capa REST (controlador, DTOs, mappers y handler)

- [x] 5.1 Crear `dto/ProfessorRequest` (`personId` con `@NotNull`), `dto/ProfessorResponse` (`id`, `centerId`, `personId`), `dto/ProfessorDetailResponse` (profesor + centro + persona) y `mapper/ProfessorRequestMapper`; verificación: `mvn compile` y el DTO de detalle expone las tres secciones.
- [x] 5.2 Crear `ProfessorController` bajo `/api/v1/centers/{centerId}/professors` con `GET` y `POST` de la colección, `GET|PUT|DELETE` del elemento y `GET .../{professorId}/detail`, cada uno anotado con `@Operation`, `@ApiResponse` y `@Tag`; verificación: `GET /api/v1/centers/1/professors` responde 200 y `POST` responde 201 con cabecera `Location`.
- [x] 5.3 Añadir a `GroupController` los endpoints `POST` y `DELETE .../groups/{groupId}/tutor/{professorId}`, ambos idempotentes y con respuesta 204; verificación: `POST .../groups/2/tutor/1` responde 204 y `GET .../groups/2` devuelve `tutorProfessorId` = 1.
- [x] 5.4 Añadir `tutorProfessorId` a `dto/GroupResponse` y un bloque `tutor` (`dto/TutorResponse` con `professorId` y la persona) a `dto/GroupDetailResponse`, alimentado por la vista ampliada; verificación: `GET .../groups/1/students/detail` responde 200 con `tutor` poblado y `GET .../groups/2/students/detail` lo responde con `tutor` nulo.
- [x] 5.5 Generalizar en `dto/PersonSummaryResponse` la descripción del `@Schema` para que no mencione sólo al estudiante; verificación: `/v3/api-docs` describe el campo de persona sin referencia exclusiva al estudiante.
- [x] 5.6 Registrar en `ApiExceptionHandler` los nuevos mapeos: `ProfessorNotFoundException` → 404, `DuplicateProfessorInCenterException` y `TutorInDifferentCenterException` → 409, y el `DataIntegrityViolationException` que mencione `UK_PROFESSOR_PERSON_CENTER` → 409 con etiqueta específica; verificación: cada excepción produce el código y el cuerpo de error esperados (`timestamp`/`status`/`error`/`message`).

## 6. Verificación de integración

- [x] 6.1 `mvn -q -DskipTests compile` finaliza con BUILD SUCCESS; verificación: salida del comando sin errores.
- [x] 6.2 `mvn -q test` finaliza sin fallos; verificación: salida del comando sin errores (el árbol de tests sigue vacío, en línea con la iteración 1).
- [x] 6.3 `mvn -q spring-boot:run` arranca con el log limpio, sin `SchemaManagementException` ni `BeanCreationException`; verificación: la aplicación responde en `/api/v1/centers`.
- [x] 6.4 Smoke manual de las reglas nuevas contra la aplicación arrancada:
  - `POST /api/v1/centers/{c}/professors` con `personId` válido → 201 con `Location`.
  - Repetirlo con la misma persona y el mismo centro → 409.
  - Repetirlo con la misma persona y otro centro → 201.
  - `GET /api/v1/centers/{c}/professors/{p}/detail` → 200 con profesor, centro y persona.
  - `POST .../groups/{g}/tutor/{p}` con un profesor de otro centro → 404 y el grupo conserva su tutor.
  - `POST .../groups/{g}/tutor/{p2}` → 204 y el tutor anterior queda reemplazado.
  - `DELETE .../groups/{g}/tutor/{p2}` → 204 y el grupo queda sin tutor.
  - `DELETE /api/v1/centers/{c}/professors/{p}` con el profesor asignando tutor → 409.
  - `DELETE /api/v1/centers/{c}` con profesores → 409.
- [x] 6.5 Revisar `/swagger-ui.html` y `/v3/api-docs`: todos los endpoints nuevos aparecen documentados y las respuestas de grupo muestran el tutor; verificación: captura del documento OpenAPI con los 7 endpoints nuevos y los campos `tutorProfessorId` y `tutor`.

## Workflow follow-up

- Archivar el cambio con `/opsx-archive add-professors-and-group-tutors` cuando todas las tareas estén marcadas.
- Verificar que el archivado crea `openspec/specs/professors/spec.md` y actualiza `openspec/specs/groups/spec.md` y `openspec/specs/centers/spec.md` con los requisitos modificados.
- Ajustar en los specs materializados el `Purpose` de `centers` y `groups` para mencionar a los profesores y al tutor, tal como queda apuntado en las preguntas abiertas del diseño.
