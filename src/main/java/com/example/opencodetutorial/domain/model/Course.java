package com.example.opencodetutorial.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Course aggregate root: an academic course that belongs to exactly one {@link Center}
 * and owns the {@link Group}s in which students are enrolled. Encapsulates the rules
 * that govern a course's lifecycle (renaming and transfer between centers) and exposes
 * them as behaviour methods rather than setters.
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
public final class Course {

    private final CourseId id;
    private final Instant createdAt;
    private CourseName name;
    private CenterId centerId;
    private Instant updatedAt;

    private Course(CourseId id,
                   CourseName name,
                   CenterId centerId,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.centerId = centerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Course} with no identifier yet (the persistence layer
     * will assign one on first save).
     *
     * @param name     the initial course name, must not be null
     * @param centerId the center that owns the course, must not be null
     * @return a new course in the "just created" state
     */
    public static Course create(CourseName name, CenterId centerId) {
        Instant now = Instant.now();
        return new Course(null,
                Objects.requireNonNull(name, "name must not be null"),
                Objects.requireNonNull(centerId, "centerId must not be null"),
                now,
                now);
    }

    /**
     * Rehydrates a {@link Course} from persistence, preserving its original identity
     * and timestamps. This factory is the only legitimate way to reconstruct an
     * existing course from outside the domain.
     *
     * @param id        the existing identifier, must not be null
     * @param name      the stored course name, must not be null
     * @param centerId  the owning center identifier, must not be null
     * @param createdAt the original creation timestamp, must not be null
     * @param updatedAt the last update timestamp, must not be null
     * @return the reconstructed course
     */
    public static Course rehydrate(CourseId id,
                                   CourseName name,
                                   CenterId centerId,
                                   Instant createdAt,
                                   Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(centerId, "centerId must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Course(id, name, centerId, createdAt, updatedAt);
    }

    /**
     * Updates the course's display name.
     *
     * @param newName the new name, must not be null
     */
    public void rename(CourseName newName) {
        this.name = Objects.requireNonNull(newName, "name must not be null");
        touch();
    }

    /**
     * Transfers the course to a different center.
     *
     * @param newCenterId the new owning center, must not be null
     */
    public void moveToCenter(CenterId newCenterId) {
        this.centerId = Objects.requireNonNull(newCenterId, "centerId must not be null");
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public CourseId id() {
        return id;
    }

    public CourseName name() {
        return name;
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