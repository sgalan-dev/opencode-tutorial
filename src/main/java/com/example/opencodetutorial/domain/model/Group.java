package com.example.opencodetutorial.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Group aggregate root.
 * Encapsulates the rules that govern a group's lifecycle (renaming) and
 * exposes them as behaviour methods rather than setters.
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
public final class Group {

    private final GroupId id;
    private final Instant createdAt;
    private GroupName name;
    private Instant updatedAt;

    private Group(GroupId id,
                  GroupName name,
                  Instant createdAt,
                  Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Group} with no identifier yet (the persistence layer
     * will assign one on first save).
     *
     * @param name the initial group name, must not be null
     * @return a new group in the "just created" state
     */
    public static Group create(GroupName name) {
        Instant now = Instant.now();
        return new Group(null, name, now, now);
    }

    /**
     * Rehydrates a {@link Group} from persistence, preserving its original identity
     * and timestamps. This factory is the only legitimate way to reconstruct an
     * existing group from outside the domain.
     *
     * @param id        the existing identifier, must not be null
     * @param name      the stored group name, must not be null
     * @param createdAt the original creation timestamp, must not be null
     * @param updatedAt the last update timestamp, must not be null
     * @return the reconstructed group
     */
    public static Group rehydrate(GroupId id,
                                  GroupName name,
                                  Instant createdAt,
                                  Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Group(id, name, createdAt, updatedAt);
    }

    /**
     * Updates the group's display name.
     *
     * @param newName the new name, must not be null
     */
    public void rename(GroupName newName) {
        this.name = Objects.requireNonNull(newName, "name must not be null");
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public GroupId id() {
        return id;
    }

    public GroupName name() {
        return name;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
