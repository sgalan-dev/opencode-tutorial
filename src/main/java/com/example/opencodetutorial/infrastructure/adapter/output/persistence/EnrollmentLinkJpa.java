package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Embeddable row of the {@code student_group_link} junction table.
 *
 * <p>The link carries a DENORMALIZED {@code course_id}: the database cannot index an
 * expression that reads {@code student_group}, and the invariant "a student is enrolled
 * in at most one group per course" has to be enforced by a unique constraint on
 * {@code (student_id, course_id)}. {@code StudentPersistenceAdapter} fills the course
 * from the group being linked; see {@code design.md}, decision 1.
 */
@Embeddable
public class EnrollmentLinkJpa {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * No-arg constructor required by the JPA provider when it rehydrates the
     * {@code @ElementCollection} rows.
     */
    protected EnrollmentLinkJpa() {
        // Used by Hibernate only.
    }

    /**
     * Builds a link row for the given group and course.
     *
     * @param groupId  the enrolled group identifier, must not be null
     * @param courseId the course that group belongs to, must not be null
     */
    public EnrollmentLinkJpa(Long groupId, Long courseId) {
        this.groupId = Objects.requireNonNull(groupId, "groupId must not be null");
        this.courseId = Objects.requireNonNull(courseId, "courseId must not be null");
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getCourseId() {
        return courseId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EnrollmentLinkJpa link)) {
            return false;
        }
        return Objects.equals(groupId, link.groupId) && Objects.equals(courseId, link.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, courseId);
    }
}