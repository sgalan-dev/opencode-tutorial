# Spec Delta

## ADDED Requirements

### Requirement: Professor is not deleted while teaching a subject
The system MUST refuse to delete a professor that is the assigned teacher of at least one subject, and SHALL NOT leave partial state behind.

#### Scenario: Professor teaches a subject
- **WHEN** the caller DELETEs a professor that is assigned to teach at least one subject
- **THEN** the system responds 409 and the professor is kept

#### Scenario: Professor no longer teaches any subject
- **WHEN** the caller DELETEs a professor after every teaching assignment of that professor has been removed
- **THEN** the system responds 204
