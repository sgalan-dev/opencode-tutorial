---
title: Second surname opcional a Person
description: afegeix el camp surname2 (segon cognom) opcional a l'entitat Person, propagat per totes les capes de l'hexàgon i recolzat per un changeset de Liquibase
author: opencode-tutorial
date: Sep 2026
version: 1.0
status: implemented
---

# Second surname opcional a Person

## Objectiu

Ampliar el model `Person` perquè admeti un **segon cognom** (`surname2`).
La motivació és reflectir convencions onomàstiques (p. ex. el doble cognom
hispànic) sense obligar que tota persona en tingui: el camp és **opcional** i
es pot ometre, arribar `null` o netejar-se en un `PUT` posterior.

## Decisions de disseny

| Decisió | Elecció | Motiu |
| --- | --- | --- |
| Obligatori o opcional? | **Opcional** (nullable) | El seed actual (Lovelace, Torvalds, Hopper, Turing) no aporta segon cognom, i no tots els models culturals l'exigeixen. |
| Modelatge en el domini | **Nou VO `SecondSurname`** que admet `null` en el seu constructor compacte | Manté el patró DDD de value objects a `domain.model` sense renunciar a la validació (no-blank + `length ≤ 100` quan arriba valor). Reutilitzar `PersonSurname` obligaria a relaxar-lo, contaminant la semàntica del camp obligatori. |
| Semàntica del blank a l'API | **Blank ⇒ 400** via `ApiExceptionHandler` | Si arriba `surname2: "   "` (o `""`), el VO llança `IllegalArgumentException`, que l'advice ja tradueix a 400. Persistir un blank com a `null` exigiria lògica al mapper que trenca la simetria amb `surname`. |
| Seeds existents | **No es toquen** | Es propaguen amb `surname2 = NULL` per la migració aditiva. |

## Canvis per capa

### Domini (`src/main/java/.../domain/model`)

- **Nou** `SecondSurname.java` — `record` amb `MAX_LENGTH = 100`. Constructor
  compacte:
  - `null` → emmagatzemat com a `null` (absència legítima).
  - valor no `null` → `trim()`, ha de ser no-buit i `length ≤ 100`; es normalitza
    amb `trim()` igual que `PersonSurname` per a una igualtat estable.
  - Helper `isEmpty()` per a futurs call sites que vulguin consultar la presència.
- **Modificat** `Person.java`:
  - Nou camp `private SecondSurname surname2;`.
  - `create(name, surname, surname2)` i `rehydrate(id, name, surname, surname2, createdAt, updatedAt)`
    amplien la seva signatura (ordre de paràmetres: `surname` abans de `surname2`).
  - Nou comportament `changeSecondSurname(SecondSurname newSurname2)`
    (paral·lel a `changeSurname`); accepta `null` per a netejar.
  - Getter `surname2()`.

### Aplicació (`src/main/java/.../application`)

- `command/CreatePersonCommand.java` i `command/UpdatePersonCommand.java`
  afegeixen `String surname2` (nullable).
- `impl/CreatePersonService.java` construeix el VO des del comandament i el passa a
  `Person.create(...)`.
- `impl/UpdatePersonService.java` invoca `existing.changeSecondSurname(...)`
  després de `changeSurname`, refrescant `updated_at` pel `touch()` intern.

### Infraestructura d'eixida (`infrastructure/adapter/output/persistence`)

- `PersonJpaEntity.java`: nou `@Column(name = "surname2", length = 100)` sense
  `nullable = false` (admet null), amb el seu getter/setter.
- `PersonPersistenceAdapter.java`:
  - `toEntity`: `entity.setSurname2(person.surname2() == null ? null : person.surname2().value())`.
  - `toDomain`: `new SecondSurname(entity.getSurname2())` (el VO tolera `null`).

### Infraestructura d'entrada (`infrastructure/adapter/input/rest`)

- `dto/PersonRequest.java`: nou camp `String surname2` amb `@Size(max = 100)`
  i **sense** `@NotBlank`; `@Schema(requiredMode = NOT_REQUIRED, nullable = true)`.
- `dto/PersonResponse.java`: nou camp `String surname2` (nullable) i
  `from(Person)` el propaga correctament.
- `mapper/PersonRequestMapper.java`: `toCreateCommand` i `toUpdateCommand`
  propaguen `request.surname2()`.
- `PersonController.java`: el `201` de `create` propaga `request.surname2()`
  en el body de resposta (que abans es construïa manualment).

### Persistència / Liquibase (`src/main/resources/db/changelog`)

- **Nou** `003-add-person-surname2.yaml` amb el changeset
  `008-add-person-surname2` (següent correlatiu després dels `001-007`):
  ```yaml
  - addColumn:
      tableName: person
      columns:
        - column:
            name: surname2
            type: varchar(100)
            constraints:
              nullable: true