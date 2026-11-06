package com.example.opencodetutorial.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Professor aggregate root: the association of one {@link Person} with one
 * {@link Center}. It is the teaching staff of a center.
 *
 * <p>Invariants:
 * <ul>
 *   <li>A professor is associated with exactly one {@link Person}.</li>
 *   <li>A professor belongs to exactly one {@link Center}, which never changes.</li>
 * </ul>
 *
 * <p>The person is a global identity, but the professorship is per center: the same
 * person may be a professor of several centers and holds one professorship per center.
 * That uniqueness is enforced by the application layer (which can see the other
 * professors) and mirrored by a {@code UNIQUE (person_id, center_id)} constraint.
 *
 * <p>A professor knows nothing about the groups it tutors: the reference to the tutor
 * lives in the {@link Group} aggregate, which keeps the graph of aggregates acyclic.
 *
 * <p>The {@link #id()} is {@code null} while the aggregate is in the "just created"
 * state and gets assigned by the persistence layer on first save. Once rehydrated
 * (via {@link #rehydrate}) the id is always present.
 *
 * <p>This class is part of the pure domain layer and MUST NOT depend on any framework
 * (Spring, JPA, Jackson, validation, etc.). Persistence concerns are handled by a
 * dedicated adapter in the infrastructure layer that translates between this aggregate
 * and a JPA entity.
 */
public final class Professor {

    private final ProfessorId id;
    private final CenterId centerId;
    private final Instant createdAt;
    private PersonId personId;
    private Instant updatedAt;

    private Professor(ProfessorId id,
                      PersonId personId,
                      CenterId centerId,
                      Instant createdAt,
                      Instant updatedAt) {
        this.id = id;
        this.personId = personId;
        this.centerId = centerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Professor} with no identifier yet (the persistence
     * layer will assign one on first save).
     *
     * @param personId the person the professor is associated with, must not be null
     * @param centerId the center the professor belongs to, must not be null
     * @return a new professor in the "just created" state
     */
    public static Professor create(PersonId personId, CenterId centerId) {
        Objects.requireNonNull(personId, "personId must not be null");
        Objects.requireNonNull(centerId, "centerId must not be null");
        Instant now = Instant.now();
        return new Professor(null, personId, centerId, now, now);
    }

    /**
     * Rehydrates a {@link Professor} from persistence, preserving its original
     * identity and timestamps. This factory is the only legitimate way to reconstruct
     * an existing professor from outside the domain.
     *
     * @param id        the existing identifier, must not be null
     * @param personId  the stored person association, must not be null
     * @param centerId  the stored center association, must not be null
     * @param createdAt the original creation timestamp, must not be null
     * @param updatedAt the last update timestamp, must not be null
     * @return the reconstructed professor
     */
    public static Professor rehydrate(ProfessorId id,
                                      PersonId personId,
                                      CenterId centerId,
                                      Instant createdAt,
                                      Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(personId, "personId must not be null");
        Objects.requireNonNull(centerId, "centerId must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Professor(id, personId, centerId, createdAt, updatedAt);
    }

    /**
     * Changes the person associated with this professor. The center stays the same:
     * a professorship belongs to one center for its whole life.
     *
     * @param newPersonId the new person, must not be null
     */
    public void changePerson(PersonId newPersonId) {
        this.personId = Objects.requireNonNull(newPersonId, "personId must not be null");
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public ProfessorId id() {
        return id;
    }

    public PersonId personId() {
        return personId;
    }

    public CenterId centerId() {
        return centerId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
