# Spec Delta

## Purpose

Permite gestionar los estudiantes que pertenecen a un centro educativo, modelando su pertenencia al centro, su asociación con una persona global y sus matriculaciones en grupos (con la regla "como máximo un grupo por curso" delegada a la capacidad `groups`).

## ADDED Requirements

### Requirement: Create student within a center
The system SHALL let a caller create a new student under the supplied center. The payload MUST reference an existing `personId` and MAY include an initial set of group identifiers in which the student is enrolled. The system MUST verify that the center exists and that the person exists before persisting. Every supplied `groupId` MUST belong to the same center and the system MUST enforce the cross-course uniqueness rule (delegated to the `groups` capability).

#### Scenario: Successful creation with no initial groups
- **WHEN** the caller POSTs a payload with a valid `personId` and `groupIds` empty or omitted
- **THEN** the system responds 201 with the new student's identifier and a `Location` header pointing at the student resource

#### Scenario: Successful creation with initial groups
- **WHEN** the caller POSTs a payload with a valid `personId` and a non-empty `groupIds` list where every group belongs to the supplied center and the course-uniqueness rule holds
- **THEN** the system responds 201 with the new student, already linked to those groups

#### Scenario: Unknown center
- **WHEN** the caller POSTs a payload under a non-existent center
- **THEN** the system responds 404 referencing the center

#### Scenario: Unknown person
- **WHEN** the caller POSTs a payload with a `personId` that does not exist
- **THEN** the system responds 404 referencing the person

### Requirement: List students of a center
The system SHALL return every student belonging to the supplied center, in no particular order. The response MAY be empty.

#### Scenario: Center has at least one student
- **WHEN** the caller GETs the students collection of an existing center
- **THEN** the system responds 200 with an array containing only that center's students

#### Scenario: Unknown center
- **WHEN** the caller GETs the students collection of a non-existent center
- **THEN** the system responds 404 referencing the center

### Requirement: Get student by identifier
The system SHALL return the student with the supplied identifiers when both the center and the student exist and the student belongs to that center.

#### Scenario: Student exists in the given center
- **WHEN** the caller GETs a student under a center that owns it
- **THEN** the system responds 200 with the student's data

#### Scenario: Student belongs to a different center
- **WHEN** the caller GETs a student under a center that does not own it
- **THEN** the system responds 404

#### Scenario: Unknown student
- **WHEN** the caller GETs a student identifier that does not exist
- **THEN** the system responds 404

### Requirement: Update student
The system SHALL replace the student's `personId`, `centerId` and the full set of `groupIds` when both the supplied center and the student exist and the student belongs to that center. All supplied `groupIds` MUST belong to the new center, and the course-uniqueness rule MUST hold.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to a student that exists and belongs to the supplied center
- **THEN** the system responds 200 with the updated student

#### Scenario: Student belongs to a different center
- **WHEN** the caller PUTs a payload to a student under a center that does not own it
- **THEN** the system responds 404

### Requirement: Delete student
The system SHALL remove the student with the supplied identifiers when both exist and the student belongs to that center. Removal MUST also remove every enrolment link the student has.

#### Scenario: Successful deletion
- **WHEN** the caller DELETEs a student under its center and the student exists
- **THEN** the system responds 204 and every enrolment that targeted the student is removed

#### Scenario: Unknown student
- **WHEN** the caller DELETEs a student identifier that does not exist
- **THEN** the system responds 404

### Requirement: Student belongs to exactly one center
The system SHALL treat every student as belonging to exactly one center at a time. A student's center can be changed via the update endpoint; a student who has been transferred to another center MUST NOT appear in the listing or be reachable via the original center's URLs.

#### Scenario: Listing after transfer
- **WHEN** a student is updated to a different center and the original center's listing is queried
- **THEN** the student does not appear in that listing
- **AND** the student appears in the new center's listing

### Requirement: Student detail with center, person and enrolments
The system SHALL return an assembled response that includes the student's own data, the center it belongs to, the personal data of the associated person, and every enrolment the student holds — each enrolment enriched with the course's and group's data.

#### Scenario: Student with enrolments in multiple courses
- **WHEN** the caller GETs the student's detail endpoint for a student enrolled in groups belonging to two different courses
- **THEN** the system responds 200 with the assembled payload containing both courses and their groups
