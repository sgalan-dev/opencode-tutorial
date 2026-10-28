# Spec Delta

## MODIFIED Requirements

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
