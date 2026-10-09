# AGENTS.md

Hexagonal Spring Boot 3.3.5 service in Java 21 + Maven. Single module, no Maven
wrapper (use the system `mvn`).

## Architecture (read this before touching code)

Strict layered packages under `src/main/java/com/example/opencodetutorial/`:

- `domain.model` — pure aggregates + value objects. **No Spring, JPA, Jackson or
  validation imports allowed.** Aggregates expose behaviour methods (`attachToGroup`,
  `changePerson`, …) and `rehydrate(...)` factory; never use public setters.
- `domain.port` — repository interfaces (`PersonRepository`, `StudentRepository`,
  `GroupRepository`).
- `domain.exception` — `*NotFoundException` types that map to HTTP 404.
- `application.usecase` — one interface per use case plus a `command` record package.
- `application.usecase.impl` — `@Service` implementations. Annotate with
  `@Transactional` only at the use-case boundary; `open-in-view` is disabled.
- `infrastructure.adapter.input.rest` — `*Controller` (under `/api/v1/...`),
  `dto`, `mapper`, and `ApiExceptionHandler` (`@RestControllerAdvice`).
- `infrastructure.adapter.output.persistence` — `*PersistenceAdapter` implements
  the domain port, owns the only `<->` translation between aggregate and `*JpaEntity`.
- `infrastructure.config` — cross-cutting beans (currently only `OpenApiConfig`).

Component scanning is anchored at
`com.example.opencodetutorial.OpencodeTutorialApplication`. New layers MUST live
under that base package.

## REST surface

All controllers are versioned under `/api/v1`:

- `persons`, `students`, `groups` — standard CRUD (`GET`, `GET /{id}`,
  `POST`, `PUT /{id}`, `DELETE /{id}`).
- Student ↔ Group membership is exposed as nested resources:
  `POST /api/v1/students/{studentId}/groups/{groupId}` to assign,
  `DELETE /api/v1/students/{studentId}/groups/{groupId}` to detach.

Error mapping lives in `ApiExceptionHandler` only. Add new mappings there;
do not catch exceptions in controllers.

## Persistence gotchas

- `spring.jpa.hibernate.ddl-auto=validate`. Schema is owned by Liquibase; never
  toggle this to `update`/`create`. New tables/columns MUST be added as a new
  changeset in `src/main/resources/db/changelog/` and included from
  `db.changelog-master.yaml`.
- The group table is named **`student_group`** because `GROUP` is a reserved SQL
  keyword. This is already noted in `001-create-schema.yaml` and the OpenAPI
  description — preserve it.
- Student ↔ Group is a many-to-many realised as a `student_group_link` junction
  with a composite PK. On the JPA side it is an `@ElementCollection` of
  `Set<Long>` on `StudentJpaEntity`; `StudentPersistenceAdapter#toEntity` clears
  and re-adds the collection in place so Hibernate emits the correct diff.
  Don't replace the `Set` reference, mutate it.
- Datasource is H2 in-memory (`jdbc:h2:mem:opencode_tutorial`), recreated on
  every restart. Local overrides go in `application-local.yml` (gitignored).

## Useful endpoints when running locally

- App: `http://localhost:8080`
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`
- H2 console: `/h2-console` (JDBC URL `jdbc:h2:mem:opencode_tutorial`, user `sa`)

## Build / run commands

Run from repo root with the system `mvn` (no wrapper):

- Compile: `mvn compile`
- Run app: `mvn spring-boot:run` (or use the VS Code launch config
  `OpencodeTutorialApplication` in `.vscode/launch.json`).
- Package: `mvn package` → runnable jar in `target/opencode-tutorial-0.1.0-SNAPSHOT.jar`.
- Tests: `mvn test` (currently `src/test/java` is empty — see
  `docs/prompts/prompt_01.md`, tests were intentionally omitted in iteration 1).

No linter, formatter or static-analysis plugin is configured; the toolchain
inherited from `spring-boot-starter-parent` is the only thing that runs in the
Maven lifecycle.

## Coding conventions (enforced by the original prompt in `docs/prompts/prompt_01.md`)

- Java 21 features are fine. Records, `var`, pattern matching allowed.
- Javadoc on **every** method, public or private, in English. Comments only in
  English; only when they add real value.
- SOLID, DDD, hexagonal architecture — keep the dependency direction
  `infrastructure -> application -> domain` and never the reverse.
- Do **not** create README/docs files unless asked.

## Inputs the prompt left to the agent's judgement

These came up during iteration 1 and are worth preserving:

- Student↔Group modelled as many-to-many (not one-to-many as the prompt's
  `group_id` field initially suggested); see the "rules" section in
  `docs/prompts/prompt_01.md`.
- Person and Student are separate aggregates. A Student references exactly one
  Person by id; deleting a Person is blocked by `fk_student_person` (`ON DELETE
  RESTRICT`).
