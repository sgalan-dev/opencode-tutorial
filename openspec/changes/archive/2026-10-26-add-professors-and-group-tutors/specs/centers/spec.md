# Spec Delta

## MODIFIED Requirements

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

#### Scenario: Center with professors
- **WHEN** the caller DELETEs a center identifier that exists and has at least one professor
- **THEN** the system responds 409 and the deletion is rejected
