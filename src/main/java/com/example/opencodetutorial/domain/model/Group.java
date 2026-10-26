package com.example.opencodetutorial.domain.model;

import com.example.opencodetutorial.domain.exception.GroupHasStudentsException;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Group aggregate root.
 * Encapsulates the rules that govern a group's lifecycle (renaming and course transfer)
 * and exposes them as behaviour methods rather than setters.
 *
 * <p>A group always belongs to exactly one {@link Course}. The enrolments live in the
 * {@link Student} aggregate (the junction is owned by the student), so this aggregate only
 * carries an {@link #enrolledStudentIds() hint} that the application layer fills when it
 * rehydrates the group; {@link #moveToCourse(CourseId)} uses it to refuse moving a group
 * that still has students, because the denormalized course of every enrolment link would
 * otherwise become stale.
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
    private CourseId courseId;
    private final Set<StudentId> enrolledStudentIds;
    private Instant updatedAt;

    private Group(GroupId id,
                  GroupName name,
                  CourseId courseId,
                  Set<StudentId> enrolledStudentIds,
                  Instant createdAt,
                  Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.enrolledStudentIds = new LinkedHashSet<>(enrolledStudentIds);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Group} with no identifier yet (the persistence layer
     * will assign one on first save).
     *
     * @param name     the initial group name, must not be null
     * @param courseId the course that owns the group, must not be null
     * @return a new group in the "just created" state
     */
    public static Group create(GroupName name, CourseId courseId) {
        Instant now = Instant.now();
        return new Group(null,
                Objects.requireNonNull(name, "name must not be null"),
                Objects.requireNonNull(courseId, "courseId must not be null"),
                Set.of(),
                now,
                now);
    }

    /**
     * Rehydrates a {@link Group} from persistence, preserving its original identity
     * and timestamps. No enrolment hint is attached: callers that need one (for
     * instance before {@link #moveToCourse(CourseId)}) use the overload that takes the
     * enrolled students.
     *
     * @param id        the existing identifier, must not be null
     * @param name      the stored group name, must not be null
     * @param courseId  the owning course identifier, must not be null
     * @param createdAt the original creation timestamp, must not be null
     * @param updatedAt the last update timestamp, must not be null
     * @return the reconstructed group
     */
    public static Group rehydrate(GroupId id,
                                  GroupName name,
                                  CourseId courseId,
                                  Instant createdAt,
                                  Instant updatedAt) {
        return rehydrate(id, name, courseId, Set.of(), createdAt, updatedAt);
    }

    /**
     * Rehydrates a {@link Group} from persistence together with the students enrolled
     * in it. Enrolments are owned by the {@link Student} aggregate, so the application
     * layer is the one that resolves them and hands them over as a hint.
     *
     * @param id                 the existing identifier, must not be null
     * @param name               the stored group name, must not be null
     * @param courseId           the owning course identifier, must not be null
     * @param enrolledStudentIds the students currently enrolled, must not be null
     * @param createdAt          the original creation timestamp, must not be null
     * @param updatedAt          the last update timestamp, must not be null
     * @return the reconstructed group
     */
    public static Group rehydrate(GroupId id,
                                  GroupName name,
                                  CourseId courseId,
                                  Set<StudentId> enrolledStudentIds,
                                  Instant createdAt,
                                  Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(courseId, "courseId must not be null");
        Objects.requireNonNull(enrolledStudentIds, "enrolledStudentIds must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Group(id, name, courseId, enrolledStudentIds, createdAt, updatedAt);
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

    /**
     * Moves the group to a different course. Refused while the group still has enrolled
     * students: the enrolment junction stores a denormalized course per link, and
     * refreshing it would require rewriting rows the {@link Student} aggregate owns.
     *
     * @param newCourseId the destination course, must not be null
     * @throws GroupHasStudentsException if the group still has enrolled students
     */
    public void moveToCourse(CourseId newCourseId) {
        Objects.requireNonNull(newCourseId, "courseId must not be null");
        if (!enrolledStudentIds.isEmpty()) {
            throw new GroupHasStudentsException(id, newCourseId);
        }
        if (!newCourseId.equals(courseId)) {
            this.courseId = newCourseId;
            touch();
        }
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

    public CourseId courseId() {
        return courseId;
    }

    /**
     * Returns an unmodifiable view of the students known to be enrolled in this group.
     *
     * @return the enrolment hint, never null (empty when it was not supplied)
     */
    public Set<StudentId> enrolledStudentIds() {
        return Collections.unmodifiableSet(enrolledStudentIds);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}