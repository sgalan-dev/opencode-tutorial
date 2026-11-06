# Spec Delta

## ADDED Requirements

### Requirement: Assign a tutor to a group
The system SHALL let a caller assign a tutor to the supplied group when the full hierarchy (center owns course, course owns group) holds and the professor belongs to that center. Assigning a tutor SHALL replace any tutor the group already had. The operation SHALL be idempotent: assigning the tutor the group already has is a no-op that returns 204.

#### Scenario: Successful assignment
- **WHEN** the caller POSTs a tutor for a group that has none
- **THEN** the system responds 204 and the group's tutor is the supplied professor

#### Scenario: Replacing the current tutor
- **WHEN** the caller POSTs a tutor for a group that already has a different tutor
- **THEN** the system responds 204 and the previous tutor is replaced by the supplied professor

#### Scenario: Idempotent assignment
- **WHEN** the caller POSTs the tutor that the group already has
- **THEN** the system responds 204 and the group's tutor does not change

#### Scenario: Professor belongs to another center
- **WHEN** the caller POSTs a professor that is not a professor of the center the group belongs to
- **THEN** the system responds 404 referencing the professor

#### Scenario: Unknown professor
- **WHEN** the caller POSTs a professor identifier that does not exist
- **THEN** the system responds 404 referencing the professor

#### Scenario: Unknown group
- **WHEN** the caller POSTs a tutor under a group that does not exist
- **THEN** the system responds 404 referencing the group

### Requirement: Remove the tutor from a group
The system SHALL let a caller remove the tutor from the supplied group when the group exists. The operation SHALL be idempotent: removing a tutor that is not the group's current tutor is a no-op that returns 204.

#### Scenario: Successful removal
- **WHEN** the caller DELETEs the tutor of a group that has one
- **THEN** the system responds 204 and the group no longer has a tutor

#### Scenario: Idempotent removal
- **WHEN** the caller DELETEs a tutor from a group that has no tutor, or whose tutor is a different professor
- **THEN** the system responds 204

#### Scenario: Unknown group
- **WHEN** the caller DELETEs a tutor under a group that does not exist
- **THEN** the system responds 404 referencing the group

### Requirement: The tutor of a group is a professor of the group's center
The system SHALL only accept as tutor of a group a professor that belongs to the center the group belongs to, where the center of a group is the center that owns the group's course.

#### Scenario: Professor of the group's center
- **WHEN** the caller assigns a professor that belongs to the same center as the group's course
- **THEN** the system accepts the assignment

#### Scenario: Professor of a different center
- **WHEN** the caller assigns a professor that belongs to another center
- **THEN** the system refuses the assignment and the group keeps its previous tutor

### Requirement: Group resource exposes its tutor
The system SHALL include the identifier of the group's tutor in every response that returns the group, and SHALL report it as absent when the group has no tutor.

#### Scenario: Group with a tutor
- **WHEN** the caller GETs a group that has a tutor
- **THEN** the response includes the identifier of that tutor

#### Scenario: Group without a tutor
- **WHEN** the caller GETs a group that has no tutor
- **THEN** the response reports the tutor as absent

## MODIFIED Requirements

### Requirement: Update group
The system SHALL replace the `name` of the group with the supplied identifiers, preserving the identifier, the center-course-group hierarchy and timestamps.

#### Scenario: Successful update
- **WHEN** the caller PUTs a valid payload to a group that exists, belongs to the supplied course, and the course belongs to the supplied center
- **THEN** the system responds 200 with the updated group

#### Scenario: The assigned tutor is preserved
- **WHEN** the caller PUTs a valid payload to a group that has a tutor
- **THEN** the system responds 200 and the group keeps its tutor

### Requirement: Group detail with course and enrolled students
The system SHALL return an assembled response that includes the group's own data, its course, the full set of enrolled students with the personal data of each one, and the group's tutor when it has one.

#### Scenario: Successful detail retrieval
- **WHEN** the caller GETs the group's detail endpoint and the group exists, its course exists, the course belongs to the supplied center, and any number of students are enrolled
- **THEN** the system responds 200 with the assembled payload

#### Scenario: Detail of a group with a tutor
- **WHEN** the caller GETs the group's detail endpoint and the group has a tutor
- **THEN** the response includes the tutor's identifier and the personal data of the person behind that tutor

#### Scenario: Detail of a group without a tutor
- **WHEN** the caller GETs the group's detail endpoint and the group has no tutor
- **THEN** the response reports the tutor as absent
