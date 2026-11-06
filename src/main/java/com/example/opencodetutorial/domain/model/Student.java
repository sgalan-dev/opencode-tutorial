package com.example.opencodetutorial.domain.model;

import com.example.opencodetutorial.domain.exception.StudentAlreadyEnrolledInCourseException;
import com.example.opencodetutorial.domain.exception.StudentInDifferentCenterException;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Student aggregate root.
 * Encapsulates the rules that govern a student's lifecycle (changing the associated
 * person, transferring between centers, joining/leaving groups) and exposes them as
 * behaviour methods rather than setters.
 *
 * <p>Invariants:
 * <ul>
 *   <li>A student is associated with exactly one {@link Person}.</li>
 *   <li>A student belongs to exactly one {@link Center}.</li>
 *   <li>A student can be enrolled in zero, one or many {@link Group}s at the same time,
 *       but at most one per {@link Course}, and every enrolment must belong to the
 *       student's own center.</li>
 * </ul>
 *
 * <p>To enforce the course rule the aggregate remembers the course of each enrolment
 * (the enrolment is a {@code GroupId -> CourseId} mapping) and checks new enrolments
 * against it, mirroring the {@code UNIQUE (student_id, course_id)} constraint on the
 * junction table.
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
    private CenterId centerId;
    private final Map<GroupId, CourseId> enrolments;
    private Instant updatedAt;

    private Student(StudentId id,
                    PersonId personId,
                    CenterId centerId,
                    Map<GroupId, CourseId> enrolments,
                    Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.personId = personId;
        this.centerId = centerId;
        this.enrolments = new LinkedHashMap<>(enrolments);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Student} with no identifier yet (the persistence layer
     * will assign one on first save). Use
     * {@link #create(PersonId, CenterId, Set)} when the student starts enrolled, and
     * then {@link #attachToGroup(Group, Course)} once per group so the course rule is
     * applied.
     *
     * @param personId      the person the student is associated with, must not be null
     * @param centerId      the center the student belongs to, must not be null
     * @param initialGroups the groups the student starts enrolled in, without their
     *                      courses; may be null or empty
     * @return a new student in the "just created" state
     */
    public static Student create(PersonId personId, CenterId centerId, Set<GroupId> initialGroups) {
        Objects.requireNonNull(personId, "personId must not be null");
        Objects.requireNonNull(centerId, "centerId must not be null");
        Instant now = Instant.now();
        Map<GroupId, CourseId> enrolments = new LinkedHashMap<>();
        if (initialGroups != null) {
            initialGroups.forEach(groupId -> enrolments.put(groupId, null));
        }
        return new Student(null, personId, centerId, enrolments, now, now);
    }

    /**
     * Rehydrates a {@link Student} from persistence, preserving its original identity,
     * center, enrolments and timestamps. This factory is the only legitimate way to
     * reconstruct an existing student from outside the domain.
     *
     * @param id          the existing identifier, must not be null
     * @param personId    the stored person association, must not be null
     * @param centerId    the stored center association, must not be null
     * @param enrolments  the stored enrolments mapped to their course; a course of
     *                    {@code null} means "not resolved yet", must not be null
     * @param createdAt   the original creation timestamp, must not be null
     * @param updatedAt   the last update timestamp, must not be null
     * @return the reconstructed student
     */
    public static Student rehydrate(StudentId id,
                                    PersonId personId,
                                    CenterId centerId,
                                    Map<GroupId, CourseId> enrolments,
                                    Instant createdAt,
                                    Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(personId, "personId must not be null");
        Objects.requireNonNull(centerId, "centerId must not be null");
        Objects.requireNonNull(enrolments, "enrolments must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Student(id, personId, centerId, enrolments, createdAt, updatedAt);
    }

    /**
     * Enrols the student in the given group. No-op (idempotent) if already enrolled.
     *
     * <p>The course that owns the group is required so the aggregate can apply the
     * "at most one group per course" and "enrolments stay inside the student's center"
     * rules itself instead of trusting the caller. It must be the course the group
     * belongs to.
     *
     * @param group  the group to enrol in, must not be null
     * @param course the course that owns {@code group}, must not be null
     * @throws StudentInDifferentCenterException if the course belongs to another center
     * @throws StudentAlreadyEnrolledInCourseException if the student is already enrolled
     *         in a different group of the same course
     */
    public void attachToGroup(Group group, Course course) {
        Objects.requireNonNull(group, "group must not be null");
        Objects.requireNonNull(course, "course must not be null");
        GroupId groupId = group.id();
        if (isAttachedToGroup(groupId)) {
            return;
        }
        if (!course.centerId().equals(centerId)) {
            throw new StudentInDifferentCenterException(id, centerId);
        }
        if (!group.courseId().equals(course.id())) {
            throw new IllegalArgumentException("Group with id " + groupId.value()
                    + " does not belong to course with id " + course.id().value());
        }
        if (enrolments.containsValue(course.id())) {
            throw new StudentAlreadyEnrolledInCourseException(id, course.id());
        }
        enrolments.put(groupId, course.id());
        touch();
    }

    /**
     * Removes the student from the given group. No-op (idempotent) if not enrolled.
     *
     * @param groupId the group to leave, must not be null
     */
    public void detachFromGroup(GroupId groupId) {
        Objects.requireNonNull(groupId, "groupId must not be null");
        if (enrolments.containsKey(groupId)) {
            enrolments.remove(groupId);
            touch();
        }
    }

    /**
     * Replaces the set of groups the student is enrolled in with the supplied one.
     * Useful for full-replacement updates from the REST layer. Every group must already
     * be known to the aggregate together with its course, otherwise the course rule
     * could not be enforced; use {@link #replaceGroups(Set, Map)} when the caller has
     * that information, or {@link #attachToGroup(Group, Course)} for incremental changes.
     *
     * @param newGroupIds the new set of groups, must not be null
     * @throws IllegalArgumentException if the course of one of the supplied groups is unknown
     */
    public void replaceGroups(Set<GroupId> newGroupIds) {
        Objects.requireNonNull(newGroupIds, "newGroupIds must not be null");
        Map<GroupId, CourseId> replacement = new LinkedHashMap<>();
        for (GroupId groupId : newGroupIds) {
            CourseId courseId = enrolments.get(groupId);
            if (courseId == null) {
                throw new IllegalArgumentException("Course of group " + groupId.value()
                        + " is unknown; use replaceGroups(Set<GroupId>, Map<GroupId, CourseId>)");
            }
            replacement.put(groupId, courseId);
        }
        applyEnrolments(replacement);
    }

    /**
     * Replaces the set of groups the student is enrolled in, taking the course of each
     * group so the "at most one group per course" rule is applied to the new set too.
     *
     * @param newGroupIds the new set of groups, must not be null
     * @param coursesByGroupId the course each supplied group belongs to, must cover
     *                        every supplied group
     * @throws StudentAlreadyEnrolledInCourseException if two supplied groups share a course
     * @throws IllegalArgumentException if a supplied group has no course
     */
    public void replaceGroups(Set<GroupId> newGroupIds, Map<GroupId, CourseId> coursesByGroupId) {
        Objects.requireNonNull(newGroupIds, "newGroupIds must not be null");
        Objects.requireNonNull(coursesByGroupId, "coursesByGroupId must not be null");
        Map<GroupId, CourseId> replacement = new LinkedHashMap<>();
        for (GroupId groupId : newGroupIds) {
            CourseId courseId = coursesByGroupId.get(groupId);
            if (courseId == null) {
                throw new IllegalArgumentException(
                        "Course of group " + groupId.value() + " is unknown");
            }
            if (replacement.containsValue(courseId)) {
                throw new StudentAlreadyEnrolledInCourseException(id, courseId);
            }
            replacement.put(groupId, courseId);
        }
        applyEnrolments(replacement);
    }

    private void applyEnrolments(Map<GroupId, CourseId> replacement) {
        if (!replacement.equals(enrolments)) {
            enrolments.clear();
            enrolments.putAll(replacement);
            touch();
        }
    }

    /**
     * Changes the person associated with this student.
     *
     * @param newPersonId the new person, must not be null
     */
    public void changePerson(PersonId newPersonId) {
        this.personId = Objects.requireNonNull(newPersonId, "personId must not be null");
        touch();
    }

    /**
     * Transfers the student to a different center.
     *
     * <p>The aggregate does not block the transfer on its own: the enrolments it holds
     * belong to the previous center, so callers replace them in the same transaction
     * (clear with {@link #replaceGroups(Set)}, call this, then set the new set).
     *
     * @param newCenterId the destination center, must not be null
     */
    public void changeCenter(CenterId newCenterId) {
        Objects.requireNonNull(newCenterId, "centerId must not be null");
        if (!newCenterId.equals(centerId)) {
            this.centerId = newCenterId;
            touch();
        }
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

    public CenterId centerId() {
        return centerId;
    }

    /**
     * Returns an unmodifiable view of the groups the student is enrolled in.
     *
     * @return the current group memberships, never null
     */
    public Set<GroupId> groupIds() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(enrolments.keySet()));
    }

    /**
     * Returns the course each enrolled group belongs to, for the groups whose course is
     * known to the aggregate.
     *
     * @return an unmodifiable view of the enrolment map, never null
     */
    public Map<GroupId, CourseId> enrolments() {
        return Collections.unmodifiableMap(enrolments);
    }

    /**
     * Returns the course of the given enrolled group.
     *
     * @param groupId the group to look up, must not be null
     * @return the course, or {@code null} when the group is not enrolled or its course
     *         is unknown to the aggregate
     */
    public CourseId courseOf(GroupId groupId) {
        Objects.requireNonNull(groupId, "groupId must not be null");
        return enrolments.get(groupId);
    }

    public boolean isAttachedToGroup(GroupId groupId) {
        Objects.requireNonNull(groupId, "groupId must not be null");
        return enrolments.containsKey(groupId);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}