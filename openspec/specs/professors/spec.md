# professors Specification

## Purpose
Permite gestionar los profesores de un centro educativo: la asociación de una persona con un centro concreto, garantizando que una misma persona no quede duplicada como profesor dentro de un mismo centro y permitiendo consultar sus datos personales junto al resto de la jerarquía.

## Requirements

### Requirement: Create professor within a center
The system SHALL let a caller create a new professor under the supplied center. The payload MUST reference an existing person. The system MUST verify that the center exists before persisting.

#### Scenario: Successful creation
- **WHEN** the caller POSTs a payload with a valid `personId` to a center that exists
- **THEN** the system responds 201 with the new professor's identifier and a `Location` header pointing at the professor resource

#### Scenario: Unknown center
- **WHEN** the caller POSTs a payload under a non-existent center
- **THEN** the system responds 404 referencing the center

#### Scenario: Unknown person
- **WHEN** the caller POSTs a payload with a `personId` that does not exist
- **THEN** the system responds 404 referencing the person

#### Scenario: Missing person
- **WHEN** the caller POSTs a payload without `personId`
- **THEN** the system responds 400 with a validation error referencing the `personId` field

### Requirement: List professors of a center
The system SHALL return every professor belonging to the supplied center, in no particular order. The response MAY be empty.

#### Scenario: Center has at least one professor
- **WHEN** the caller GETs the professors collection of an existing center
- **THEN** the system responds 200 with an array containing only that center's professors

#### Scenario: Center has no professors
- **WHEN** the caller GETs the professors collection of an existing center with no professors
- **THEN** the system responds 200 with an empty array

#### Scenario: Unknown center
- **WHEN** the caller GETs the professors collection of a non-existent center
- **THEN** the system responds 404 referencing the center

### Requirement: Get professor by identifier
The system SHALL return the professor with the supplied identifiers when both the center and the professor exist and the professor belongs to that center.

#### Scenario: Professor exists in the given center
- **WHEN** the caller GETs a professor under a center that owns it
- **THEN** the system responds 200 with the professor's data

#### Scenario: Professor belongs to a different center
- **WHEN** the caller GETs a professor under a center that does not own it
- **THEN** the system responds 404

#### Scenario: Unknown professor
- **WHEN** the caller GETs a professor identifier that does not exist
- **THEN** the system responds 404

### Requirement: Update professor
The system SHALL replace the person associated with the professor with the supplied identifiers, when both the center and the professor exist and the professor belongs to that center. The new person MUST exist.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to a professor that exists and belongs to the supplied center
- **THEN** the system responds 200 with the updated professor

#### Scenario: Unknown person
- **WHEN** the caller PUTs a payload whose `personId` does not exist
- **THEN** the system responds 404 referencing the person

#### Scenario: Missing person
- **WHEN** the caller PUTs a payload without `personId`
- **THEN** the system responds 400 with a validation error referencing the `personId` field

#### Scenario: Professor belongs to a different center
- **WHEN** the caller PUTs a payload to a professor under a center that does not own it
- **THEN** the system responds 404

### Requirement: Delete professor
The system SHALL remove the professor with the supplied identifiers when both exist and the professor belongs to that center.

#### Scenario: Successful deletion
- **WHEN** the caller DELETEs a professor under its center and the professor is not the tutor of any group
- **THEN** the system responds 204

#### Scenario: Unknown professor
- **WHEN** the caller DELETEs a professor identifier that does not exist
- **THEN** the system responds 404

### Requirement: Professor is not deleted while tutoring a group
The system MUST refuse to delete a professor that is the tutor of at least one group, and SHALL NOT leave partial state behind.

#### Scenario: Professor tutors a group
- **WHEN** the caller DELETEs a professor that is assigned as tutor of at least one group
- **THEN** the system responds 409 and the professor is kept

#### Scenario: Professor no longer tutors any group
- **WHEN** the caller DELETEs a professor after every tutoring assignment of that professor has been removed
- **THEN** the system responds 204

### Requirement: At most one professor per person within a center
The system MUST prevent a person from being registered twice as a professor within the same center. The rule SHALL be enforced in the application domain and also in the database so that direct persistence cannot bypass it.

#### Scenario: Duplicate rejected by the domain
- **WHEN** the caller creates or updates a professor so that the same person would hold two professorships in the same center
- **THEN** the system responds 409 explaining that the person is already a professor of that center

#### Scenario: Duplicate cannot be inserted directly bypassing the domain
- **WHEN** an attempt is made (through any path that writes directly to the professor table) to insert a second row for the same `(person_id, center_id)`
- **THEN** the database MUST reject the operation with a unique-constraint violation, and the application MUST surface it as 409

### Requirement: A person may be professor of several centers
The system SHALL allow the same person to hold a professorship in more than one center, each professorship being independent from the others.

#### Scenario: Same person in two centers
- **WHEN** the caller creates a professor for a person that is already a professor of another center
- **THEN** the system responds 201 and the person holds two independent professorships

#### Scenario: Professorships are listed per center
- **WHEN** the caller lists the professors of two different centers where the same person is a professor
- **THEN** each listing contains only the professorship that belongs to its own center

### Requirement: Professor detail with center and person
The system SHALL return an assembled response that includes the professor's own data, the center it belongs to, and the personal data of the associated person.

#### Scenario: Successful detail retrieval
- **WHEN** the caller GETs the professor's detail endpoint and the professor exists and belongs to the supplied center
- **THEN** the system responds 200 with the assembled payload

#### Scenario: Unknown professor
- **WHEN** the caller GETs the detail of a professor identifier that does not exist
- **THEN** the system responds 404
