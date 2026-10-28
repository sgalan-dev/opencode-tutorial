# Spec Delta

## Purpose

Permite gestionar las asignaturas (Subjects) que se imparten en los cursos de un centro educativo: cada asignatura pertenece a un curso, puede tener un profesor que la imparte como especialista y puede quedarse sin profesor, que es un estado legítimo mientras nadie la imparte.

## ADDED Requirements

### Requirement: Create subject within a course
The system SHALL let a caller create a new subject under the supplied center and course. `name` MUST be non-blank and non-null. The created subject SHALL belong to that course and to no other. The system MUST verify that the center exists and that the course exists and belongs to it before persisting.

#### Scenario: Successful creation
- **WHEN** the caller POSTs a payload with `name` "Matemáticas" to a course that exists and belongs to the supplied center
- **THEN** the system responds 201 with the new subject's identifier and a `Location` header pointing at the subject resource

#### Scenario: Unknown center
- **WHEN** the caller POSTs a payload under a center that does not exist
- **THEN** the system responds 404 referencing the center

#### Scenario: Course does not belong to center
- **WHEN** the caller POSTs a payload under a course that exists but belongs to a different center than the URL specifies
- **THEN** the system responds 404 referencing the course

#### Scenario: Blank or missing name
- **WHEN** the caller POSTs a payload with `name` null, blank or whitespace-only
- **THEN** the system responds 400 with a validation error referencing the `name` field

#### Scenario: Duplicate name in the same course
- **WHEN** the caller POSTs a payload with a `name` already used by another subject of the same course
- **THEN** the system responds 409

### Requirement: List subjects of a course
The system SHALL return every subject belonging to the supplied course, in no particular order. The response MAY be empty.

#### Scenario: Course has at least one subject
- **WHEN** the caller GETs the subjects collection of a course that exists and belongs to the supplied center
- **THEN** the system responds 200 with an array containing only that course's subjects

#### Scenario: Course has no subjects
- **WHEN** the caller GETs the subjects collection of an existing course with no subjects
- **THEN** the system responds 200 with an empty array

#### Scenario: Unknown course
- **WHEN** the caller GETs the subjects collection of a course that does not exist
- **THEN** the system responds 404 referencing the course

### Requirement: Get subject by identifier
The system SHALL return the subject with the supplied identifiers when the center exists, the course exists and belongs to that center, and the subject exists and belongs to that course.

#### Scenario: Subject exists in the given course
- **WHEN** the caller GETs a subject under a course that owns it and a center that owns the course
- **THEN** the system responds 200 with the subject's data

#### Scenario: Subject belongs to a different course
- **WHEN** the caller GETs a subject under a course that does not own it
- **THEN** the system responds 404

#### Scenario: Unknown subject
- **WHEN** the caller GETs a subject identifier that does not exist
- **THEN** the system responds 404

### Requirement: Update subject
The system SHALL replace the `name` of the subject with the supplied identifiers, preserving the identifier, the course the subject belongs to and the professor it may have.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to a subject that exists and belongs to the supplied course
- **THEN** the system responds 200 with the updated subject

#### Scenario: The assigned professor is preserved
- **WHEN** the caller PUTs a valid payload to a subject that has a professor
- **THEN** the system responds 200 and the subject keeps its professor

#### Scenario: Blank or missing name
- **WHEN** the caller PUTs a payload with `name` null, blank or whitespace-only
- **THEN** the system responds 400 with a validation error referencing the `name` field

#### Scenario: Duplicate name in the same course
- **WHEN** the caller PUTs a payload with a `name` already used by another subject of the same course
- **THEN** the system responds 409

### Requirement: Delete subject
The system SHALL remove the subject with the supplied identifiers when the center exists, the course exists and belongs to it, and the subject exists and belongs to that course.

#### Scenario: Successful deletion
- **WHEN** the caller DELETEs a subject under the course that owns it
- **THEN** the system responds 204

#### Scenario: Unknown subject
- **WHEN** the caller DELETEs a subject identifier that does not exist
- **THEN** the system responds 404

#### Scenario: Subject belongs to a different course
- **WHEN** the caller DELETEs a subject under a course that does not own it
- **THEN** the system responds 404

### Requirement: Assign a professor to a subject
The system SHALL let a caller assign the professor that teaches a subject, replacing the professor it had, if any. The operation SHALL be idempotent: assigning the professor the subject already has is a no-op that returns 204.

#### Scenario: Successful assignment
- **WHEN** the caller POSTs a professor for a subject that has none
- **THEN** the system responds 204 and the subject's professor is the supplied professor

#### Scenario: Replacing the current professor
- **WHEN** the caller POSTs a professor for a subject that already has a different professor
- **THEN** the system responds 204 and the previous professor is replaced by the supplied professor

#### Scenario: Idempotent assignment
- **WHEN** the caller POSTs the professor that the subject already has
- **THEN** the system responds 204 and the subject's professor does not change

#### Scenario: Unknown subject
- **WHEN** the caller POSTs a professor under a subject that does not exist
- **THEN** the system responds 404 referencing the subject

#### Scenario: Unknown professor
- **WHEN** the caller POSTs a professor identifier that does not exist
- **THEN** the system responds 404 referencing the professor

#### Scenario: Professor belongs to another center
- **WHEN** the caller POSTs a professor that is not a professor of the center the subject's course belongs to
- **THEN** the system responds 404 referencing the professor

### Requirement: Remove the professor from a subject
The system SHALL let a caller remove the professor that teaches a subject. The operation SHALL be idempotent: removing a professor that is not the subject's current professor is a no-op that returns 204.

#### Scenario: Successful removal
- **WHEN** the caller DELETEs the professor of a subject that has one
- **THEN** the system responds 204 and the subject no longer has a professor

#### Scenario: Idempotent removal
- **WHEN** the caller DELETEs a professor from a subject that has no professor, or whose professor is a different professor
- **THEN** the system responds 204

#### Scenario: Unknown subject
- **WHEN** the caller DELETEs a professor under a subject that does not exist
- **THEN** the system responds 404 referencing the subject

### Requirement: The professor of a subject is a professor of the course's center
The system SHALL only accept as the professor of a subject a professor that belongs to the center the subject's course belongs to.

#### Scenario: Professor of the course's center
- **WHEN** the caller assigns a professor that belongs to the same center as the subject's course
- **THEN** the system accepts the assignment

#### Scenario: Professor of a different center
- **WHEN** the caller assigns a professor that belongs to another center
- **THEN** the system refuses the assignment and the subject keeps its previous professor

### Requirement: Subject name uniqueness within a course
The system SHALL enforce that a subject's `name` is unique within its course. Two different courses MAY share the same subject `name`.

#### Scenario: Duplicate name in the same course
- **WHEN** the caller POSTs or PUTs a subject with a `name` already used by another subject under the same course
- **THEN** the system responds 409

#### Scenario: Same name across different courses
- **WHEN** the caller creates a subject with a `name` that another subject under a different course uses
- **THEN** the creation succeeds

### Requirement: Subject resource exposes its professor
The system SHALL include the identifier of the subject's professor in every response that returns the subject, and SHALL report it as absent when the subject has no professor.

#### Scenario: Subject with a professor
- **WHEN** the caller GETs a subject that has a professor
- **THEN** the response includes the identifier of that professor

#### Scenario: Subject without a professor
- **WHEN** the caller GETs a subject that has no professor
- **THEN** the response reports the professor as absent
