package com.example.opencodetutorial.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Student aggregate root.
 * Encapsulates the rules that govern a student's lifecycle (changing the
 * associated person, joining/leaving groups) and exposes them as behaviour
 * methods rather than setters.
 *
 * <p>Invariants:
 * <ul>
 *   <li>A student is associated with exactly one {@link Person}.</li>
 *   <li>A student can be enrolled in zero, one or many {@link Group}s at the same time.</li>
 * </ul>
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
public final class Student {

    private final StudentId id;
    private final Instant createdAt;
    private PersonId personId;
    private final Set<GroupId> groupIds;
    private Instant updatedAt;

    private Student(StudentId id,
                    PersonId personId,
                    Set<GroupId> groupIds,
                    Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.personId = personId;
        // Preserve insertion order so callers and persistence see a stable sequence.
        this.groupIds = new LinkedHashSet<>(groupIds);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Student} with no identifier yet (the persistence layer
     * will assign one on first save). The student is created already enrolled in the
     * supplied groups; an empty (or null) collection means "not enrolled yet".
     *
     * @param personId     the person the student is associated with, must not be null
     * @param initialGroups the initial set of groups, may be null or empty
     * @return a new student in the "just created" state
     */
    public static Student create(PersonId personId, Set<GroupId> initialGroups) {
        Objects.requireNonNull(personId, "personId must not be null");
        Instant now = Instant.now();
        Set<GroupId> safeGroups = initialGroups == null ? Set.of() : initialGroups;
        return new Student(null, personId, safeGroups, now, now);
    }

    /**
     * Rehydrates a {@link Student} from persistence, preserving its original identity,
     * group memberships and timestamps. This factory is the only legitimate way to
     * reconstruct an existing student from outside the domain.
     *
     * @param id          the existing identifier, must not be null
     * @param personId    the stored person association, must not be null
     * @param groupIds    the stored group memberships, must not be null
     * @param createdAt   the original creation timestamp, must not be null
     * @param updatedAt   the last update timestamp, must not be null
     * @return the reconstructed student
     */
    public static Student rehydrate(StudentId id,
                                    PersonId personId,
                                    Set<GroupId> groupIds,
                                    Instant createdAt,
                                    Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(personId, "personId must not be null");
        Objects.requireNonNull(groupIds, "groupIds must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Student(id, personId, groupIds, createdAt, updatedAt);
    }

    /**
     * Enrols the student in the given group. No-op (idempotent) if already enrolled.
     *
     * @param groupId the group to enrol in, must not be null
     */
    public void attachToGroup(GroupId groupId) {
        Objects.requireNonNull(groupId, "groupId must not be null");
        if (groupIds.add(groupId)) {
            touch();
        }
    }

    /**
     * Removes the student from the given group. No-op (idempotent) if not enrolled.
     *
     * @param groupId the group to leave, must not be null
     */
    public void detachFromGroup(GroupId groupId) {
        Objects.requireNonNull(groupId, "groupId must not be null");
        if (groupIds.remove(groupId)) {
            touch();
        }
    }

    /**
     * Replaces the set of groups the student is enrolled in with the supplied one.
     * Useful for full-replacement updates from the REST layer.
     *
     * @param newGroupIds the new set of groups, must not be null
     */
    public void replaceGroups(Set<GroupId> newGroupIds) {
        Objects.requireNonNull(newGroupIds, "newGroupIds must not be null");
        Set<GroupId> replacement = new LinkedHashSet<>(newGroupIds);
        if (!replacement.equals(groupIds)) {
            groupIds.clear();
            groupIds.addAll(replacement);
            touch();
        }
    }

    /**
     * Changes the person associated with this student.
     *
     * @param newPersonId the new person, must not be null
     */
    public void changePerson(PersonId newPersonId) {
        this.personId = Objects.requireNonNull(newPersonId, "newPersonId must not be null");
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public StudentId id() {
        return id;
    }

    public PersonId personId() {
        return personId;
    }

    /**
     * Returns an unmodifiable view of the groups the student is enrolled in.
     *
     * @return the current group memberships, never null
     */
    public Set<GroupId> groupIds() {
        return Collections.unmodifiableSet(groupIds);
    }

    public boolean isAttachedToGroup(GroupId groupId) {
        Objects.requireNonNull(groupId, "groupId must not be null");
        return groupIds.contains(groupId);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
