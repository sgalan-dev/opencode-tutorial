# Spec Delta

## Purpose

Permite gestionar los centros educativos (Centros) que son la entidad raíz de la jerarquía: a un centro pertenecen cursos, y dentro de los cursos existen los grupos en los que se matriculan los estudiantes.

## ADDED Requirements

### Requirement: Create educational center
The system SHALL let an authenticated caller create a new educational center by supplying a non-blank, non-null `name`. The system MUST persist the center and return its assigned identifier.

#### Scenario: Successful creation
- **WHEN** the caller POSTs a payload with `name` "IES Montecastelo"
- **THEN** the system responds 201, returns the new center's identifier and a `Location` header pointing to it

#### Scenario: Blank or missing name
- **WHEN** the caller POSTs a payload with `name` null, blank or whitespace-only
- **THEN** the system responds 400 with a validation error referencing the `name` field

### Requirement: List educational centers
The system SHALL return every persisted educational center, in no particular order, on request. The response MAY be empty.

#### Scenario: At least one center exists
- **WHEN** the caller GETs the centers collection and at least one center is persisted
- **THEN** the system responds 200 with an array containing all centers

#### Scenario: No center exists
- **WHEN** the caller GETs the centers collection and no center is persisted
- **THEN** the system responds 200 with an empty array

### Requirement: Get educational center by identifier
The system SHALL return the educational center with the supplied identifier.

#### Scenario: Existing center
- **WHEN** the caller GETs a center with an identifier that exists
- **THEN** the system responds 200 with that center's data

#### Scenario: Unknown center
- **WHEN** the caller GETs a center with an identifier that does not exist
- **THEN** the system responds 404

### Requirement: Update educational center
The system SHALL replace the `name` of the educational center with the supplied identifier, preserving the identifier and timestamps.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to an existing center
- **THEN** the system responds 200 with the updated center

#### Scenario: Unknown center
- **WHEN** the caller PUTs a payload to a center identifier that does not exist
- **THEN** the system responds 404

### Requirement: Delete educational center
The system SHALL remove the educational center with the supplied identifier. The center MUST NOT be deleted while it has dependent courses, students or child entities; in that case the system MUST refuse with 409 or an equivalent conflict response and SHALL NOT leave partial state.

#### Scenario: Successful deletion
- **WHEN** the caller DELETEs a center identifier that exists and has no dependents
- **THEN** the system responds 204

#### Scenario: Unknown center
- **WHEN** the caller DELETEs a center identifier that does not exist
- **THEN** the system responds 404

#### Scenario: Center with dependents
- **WHEN** the caller DELETEs a center identifier that exists and has at least one dependent (course, student, etc.)
- **THEN** the system responds 409 and the deletion is rejected

### Requirement: Center name uniqueness
The system SHALL treat `name` as globally unique across educational centers. A second center MUST NOT be created or renamed to reuse a name already in use.

#### Scenario: Duplicate name on creation
- **WHEN** the caller POSTs a payload with a `name` that already exists
- **THEN** the system responds 409

#### Scenario: Duplicate name on rename
- **WHEN** the caller PUTs a payload whose `name` already belongs to a different center
- **THEN** the system responds 409
