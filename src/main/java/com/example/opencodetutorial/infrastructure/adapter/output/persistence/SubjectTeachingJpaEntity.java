package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for the {@code subject_teaching} table, the materialisation of the
 * relation between a course, one of its subjects and the professor that teaches it.
 *
 * <p>{@code subjectId} is the primary key, because a subject has at most one professor:
 * the database refuses a second teaching row for the same subject. It is the second
 * layer of the domain rule enforced by {@code Subject.assignProfessor}.
 *
 * <p>{@code courseId} is denormalized from the {@code subject} table on purpose: the
 * row carries the whole chain and the database can therefore check it without joining.
 */
@Entity
@Table(name = "subject_teaching")
public class SubjectTeachingJpaEntity {

    @Id
    @Column(name = "subject_id", nullable = false, updatable = false)
    private Long subjectId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }
}
