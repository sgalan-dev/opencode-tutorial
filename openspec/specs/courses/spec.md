# courses Specification

## Purpose

Permite gestionar los cursos académicos (Cursos) que pertenecen a un centro educativo. Un curso agrupa varios grupos y restringe la regla "un estudiante pertenece como máximo a un grupo del mismo curso".

## Requirements

### Requirement: Create course within a center
The system SHALL let a caller create a new course under the supplied center identifier. `name` MUST be a non-blank, non-null string. The system MUST verify that the supplied `centerId` exists before persisting.

#### Scenario: Successful creation
- **WHEN** the caller POSTs a payload with `name` "1º DAM" to a center that exists
- **THEN** the system responds 201 with the new course's identifier and a `Location` header pointing at `/api/v1/centers/{centerId}/courses/{courseId}`

#### Scenario: Unknown center
- **WHEN** the caller POSTs a payload to a center identifier that does not exist
- **THEN** the system responds 404 referencing the center

#### Scenario: Blank or missing name
- **WHEN** the caller POSTs a payload with `name` null, blank or whitespace-only
- **THEN** the system responds 400 with a validation error referencing the `name` field

### Requirement: List courses of a center
The system SHALL return every course belonging to the supplied center, in no particular order. The response MAY be empty.

#### Scenario: Center has at least one course
- **WHEN** the caller GETs the courses collection of an existing center
- **THEN** the system responds 200 with an array containing only that center's courses

#### Scenario: Center has no courses
- **WHEN** the caller GETs the courses collection of an existing center with no courses
- **THEN** the system responds 200 with an empty array

#### Scenario: Unknown center
- **WHEN** the caller GETs the courses collection of a non-existent center
- **THEN** the system responds 404 referencing the center

### Requirement: Get course by identifier
The system SHALL return the course with the supplied identifiers when both the center and the course exist and the course belongs to that center.

#### Scenario: Course exists in the given center
- **WHEN** the caller GETs a course under a center that owns it
- **THEN** the system responds 200 with the course's data

#### Scenario: Course belongs to a different center
- **WHEN** the caller GETs a course under a center that does not own it
- **THEN** the system responds 404

#### Scenario: Unknown course
- **WHEN** the caller GETs a course identifier that does not exist
- **THEN** the system responds 404

### Requirement: Update course
The system SHALL replace the `name` of the course with the supplied identifiers when both the center and the course exist and the course belongs to that center.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to a course that exists and belongs to the supplied center
- **THEN** the system responds 200 with the updated course

#### Scenario: Course belongs to a different center
- **WHEN** the caller PUTs a payload to a course under a center that does not own it
- **THEN** the system responds 404

### Requirement: Delete course
The system SHALL remove the course with the supplied identifiers when both exist and the course belongs to that center. The course MUST NOT be deleted while it has dependent groups or dependent subjects.

#### Scenario: Successful deletion
- **WHEN** the caller DELETEs a course under its center and the course has no dependent groups and no dependent subjects
- **THEN** the system responds 204

#### Scenario: Unknown course
- **WHEN** the caller DELETEs a course identifier that does not exist
- **THEN** the system responds 404

#### Scenario: Course with dependent groups
- **WHEN** the caller DELETEs a course under its center and the course has at least one group
- **THEN** the system responds 409 and the deletion is rejected

#### Scenario: Course with dependent subjects
- **WHEN** the caller DELETEs a course under its center and the course has at least one subject
- **THEN** the system responds 409 and the deletion is rejected

### Requirement: Course name uniqueness within a center
The system SHALL enforce that a course's `name` is unique within its center. Two different centers MAY share the same course `name`.

#### Scenario: Duplicate name in same center
- **WHEN** the caller POSTs or PUTs a course with a `name` already used by another course under the same center
- **THEN** the system responds 409

#### Scenario: Same name across different centers
- **WHEN** the caller creates a course with a `name` that another course under a different center uses
- **THEN** the creation succeeds (no uniqueness conflict across centers)
