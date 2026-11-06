package com.example.opencodetutorial.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Center aggregate root: the root of the Center -&gt; Course -&gt; Group -&gt; Student
 * hierarchy. Encapsulates the rules that govern a center's lifecycle (renaming) and
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
public final class Center {

    private final CenterId id;
    private final Instant createdAt;
    private CenterName name;
    private Instant updatedAt;

    private Center(CenterId id,
                   CenterName name,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Center} with no identifier yet (the persistence layer
     * will assign one on first save).
     *
     * @param name the initial center name, must not be null
     * @return a new center in the "just created" state
     */
    public static Center create(CenterName name) {
        Instant now = Instant.now();
        return new Center(null, Objects.requireNonNull(name, "name must not be null"), now, now);
    }

    /**
     * Rehydrates a {@link Center} from persistence, preserving its original identity
     * and timestamps. This factory is the only legitimate way to reconstruct an
     * existing center from outside the domain.
     *
     * @param id        the existing identifier, must not be null
     * @param name      the stored center name, must not be null
     * @param createdAt the original creation timestamp, must not be null
     * @param updatedAt the last update timestamp, must not be null
     * @return the reconstructed center
     */
    public static Center rehydrate(CenterId id,
                                   CenterName name,
                                   Instant createdAt,
                                   Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Center(id, name, createdAt, updatedAt);
    }

    /**
     * Updates the center's display name.
     *
     * @param newName the new name, must not be null
     */
    public void rename(CenterName newName) {
        this.name = Objects.requireNonNull(newName, "name must not be null");
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public CenterId id() {
        return id;
    }

    public CenterName name() {
        return name;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}