# Spec Delta

## Purpose

Permite gestionar los grupos (clases) que existen dentro de cada curso de un centro educativo, así como las operaciones de matrícula y desmatrícula de estudiantes en cada grupo, aplicando la regla de unicidad "un estudiante pertenece como máximo a un grupo del mismo curso" tanto en el dominio como en la base de datos.

## ADDED Requirements

### Requirement: Create group within a course
The system SHALL let a caller create a new group under the supplied center and course. `name` MUST be non-blank and non-null. The system MUST verify that the supplied center exists, that the course exists and belongs to the center, before persisting.

#### Scenario: Successful creation
- **WHEN** the caller POSTs a payload with `name` "Grupo A" to a course that exists and belongs to the supplied center
- **THEN** the system responds 201 with the new group's identifier and a `Location` header pointing at the group resource

#### Scenario: Unknown center
- **WHEN** the caller POSTs a payload under a non-existent center
- **THEN** the system responds 404 referencing the center

#### Scenario: Course does not belong to center
- **WHEN** the caller POSTs a payload under a course that exists but belongs to a different center than the URL specifies
- **THEN** the system responds 404 referencing the course

#### Scenario: Blank or missing name
- **WHEN** the caller POSTs a payload with `name` null, blank or whitespace-only
- **THEN** the system responds 400 with a validation error referencing the `name` field

### Requirement: List groups of a course
The system SHALL return every group belonging to the supplied course, in no particular order. The response MAY be empty.

#### Scenario: Course has at least one group
- **WHEN** the caller GETs the groups collection of a course that exists and belongs to the supplied center
- **THEN** the system responds 200 with an array containing only that course's groups

#### Scenario: Course has no groups
- **WHEN** the caller GETs the groups collection of an existing course with no groups
- **THEN** the system responds 200 with an empty array

#### Scenario: Course belongs to a different center
- **WHEN** the caller GETs the groups collection of a course under a center that does not own it
- **THEN** the system responds 404 referencing the course

### Requirement: Get group by identifier
The system SHALL return the group with the supplied identifiers when both the center and the course exist, the course belongs to the center, and the group belongs to the course.

#### Scenario: Group exists in the given course
- **WHEN** the caller GETs a group under a course that owns it and a center that owns the course
- **THEN** the system responds 200 with the group's data

#### Scenario: Group belongs to a different course
- **WHEN** the caller GETs a group under a course that does not own it
- **THEN** the system responds 404

#### Scenario: Unknown group
- **WHEN** the caller GETs a group identifier that does not exist
- **THEN** the system responds 404

### Requirement: Update group
The system SHALL replace the `name` of the group with the supplied identifiers, preserving the identifier, the center-course-group hierarchy and timestamps.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to a group that exists, belongs to the supplied course, and the course belongs to the supplied center
- **THEN** the system responds 200 with the updated group

### Requirement: Delete group
The system SHALL remove the group with the supplied identifiers when the full hierarchy (center owns course, course owns group) holds. Removing a group SHALL also remove every student enrolment that targets that group.

#### Scenario: Successful deletion
- **WHEN** the caller DELETEs a group under its course and center and no student references it elsewhere
- **THEN** the system responds 204 and every enrolment that targeted the group is removed

#### Scenario: Unknown group
- **WHEN** the caller DELETEs a group identifier that does not exist
- **THEN** the system responds 404

### Requirement: Enrol a student in a group
The system SHALL enrol the supplied student in the supplied group when the full hierarchy (center owns course, course owns group, student belongs to center) holds. The operation SHALL be idempotent: enrolling a student already in the group is a no-op that returns 204.

#### Scenario: Successful first-time enrolment
- **WHEN** the caller POSTs an enrolment to a group that does not yet include the student
- **THEN** the system responds 204 and the student's enrolment set now includes the group

#### Scenario: Idempotent enrolment
- **WHEN** the caller POSTs an enrolment to a group that already includes the student
- **THEN** the system responds 204 and no duplicate link row is created

### Requirement: Detach a student from a group
The system SHALL remove the supplied student from the supplied group when the student exists. The operation SHALL be idempotent: removing a student not in the group is a no-op that returns 204.

#### Scenario: Successful detachment
- **WHEN** the caller DELETEs an enrolment between a student and a group that exists
- **THEN** the system responds 204 and the student's enrolment set no longer includes the group

#### Scenario: Idempotent detachment
- **WHEN** the caller DELETEs an enrolment between a student and a group in which the student is not enrolled
- **THEN** the system responds 204

### Requirement: At most one group per course per student
The system MUST prevent a single student from being enrolled simultaneously in two or more groups that share the same course. The rule SHALL be enforced in the application domain (rich error response) and also in the database (a unique constraint on the enrolment link per `(student_id, course_id)`) so that direct persistence cannot bypass it.

#### Scenario: Enrolment rejected by the domain
- **WHEN** the caller enrols a student in a group that belongs to the same course as another group in which the student is already enrolled
- **THEN** the system responds 409 with a message explaining the student is already enrolled in a group of that course

#### Scenario: Link cannot be inserted directly bypassing the domain
- **WHEN** an attempt is made (through any path that writes directly to the enrolment link table) to insert a second row for the same `(student_id, course_id)`
- **THEN** the database MUST reject the operation with a unique-constraint violation, and the application MUST surface it as 409

### Requirement: List students enrolled in a group
The system SHALL return every student currently enrolled in the supplied group, when the full hierarchy holds.

#### Scenario: Group has at least one enrollee
- **WHEN** the caller GETs the enrollees collection of a group that exists and has at least one enrollee
- **THEN** the system responds 200 with an array of student identifiers (and basic student data, per implementation choice)

#### Scenario: Group has no enrolees
- **WHEN** the caller GETs the enrollees collection of an existing empty group
- **THEN** the system responds 200 with an empty array

### Requirement: Group detail with course and enrolled students
The system SHALL return an assembled response that includes the group's own data, its course, and the full set of enrolled students with the personal data of each one.

#### Scenario: Successful detail retrieval
- **WHEN** the caller GETs the group's detail endpoint and the group exists, its course exists, the course belongs to the supplied center, and any number of students are enrolled
- **THEN** the system responds 200 with the assembled payload
