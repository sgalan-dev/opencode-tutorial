package com.example.opencodetutorial.domain.model;

import com.example.opencodetutorial.domain.exception.SubjectProfessorInDifferentCenterException;

import java.time.Instant;
import java.util.Objects;

/**
 * Subject aggregate root: an academic subject that belongs to exactly one {@link Course}
 * and may be taught by one {@link Professor}. Encapsulates the rules that govern a
 * subject's lifecycle (renaming and the assignment of the professor that teaches it)
 * and exposes them as behaviour methods rather than setters.
 *
 * <p>A subject always belongs to exactly one course. The professor that teaches it is
 * the specialist of that course: it must be a professor of the center that owns the
 * course, exactly like the tutor of a {@link Group} is a professor of the center that
 * owns the group's course.
 *
 * <p>The reference to the professor lives here and not in the {@link Professor}
 * aggregate, so the graph of aggregates stays acyclic and "who teaches what" remains a
 * read question. It is persisted in its own table ({@code subject_teaching}), which is
 * transparent for this aggregate: it only knows the identifier of the professor.
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
public final class Subject {

    private final SubjectId id;
    private final Instant createdAt;
    private SubjectName name;
    private CourseId courseId;
    private ProfessorId professorId;
    private Instant updatedAt;

    private Subject(SubjectId id,
                    SubjectName name,
                    CourseId courseId,
                    ProfessorId professorId,
                    Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.professorId = professorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Subject} with no identifier yet (the persistence layer
     * will assign one on first save). A new subject has no professor: the assignment
     * is a later, explicit operation.
     *
     * @param name     the initial subject name, must not be null
     * @param courseId the course that owns the subject, must not be null
     * @return a new subject in the "just created" state
     */
    public static Subject create(SubjectName name, CourseId courseId) {
        Instant now = Instant.now();
        return new Subject(null,
                Objects.requireNonNull(name, "name must not be null"),
                Objects.requireNonNull(courseId, "courseId must not be null"),
                null,
                now,
                now);
    }

    /**
     * Rehydrates a {@link Subject} from persistence, preserving its original identity,
     * timestamps and the professor that teaches it, if any.
     *
     * @param id          the existing identifier, must not be null
     * @param name        the stored subject name, must not be null
     * @param courseId    the owning course identifier, must not be null
     * @param professorId the stored professor, or {@code null} when the subject has none
     * @param createdAt   the original creation timestamp, must not be null
     * @param updatedAt   the last update timestamp, must not be null
     * @return the reconstructed subject
     */
    public static Subject rehydrate(SubjectId id,
                                    SubjectName name,
                                    CourseId courseId,
                                    ProfessorId professorId,
                                    Instant createdAt,
                                    Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(courseId, "courseId must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Subject(id, name, courseId, professorId, createdAt, updatedAt);
    }

    /**
     * Updates the subject's display name. The professor that teaches it is not
     * touched: the assignment has its own operation.
     *
     * @param newName the new name, must not be null
     */
    public void rename(SubjectName newName) {
        this.name = Objects.requireNonNull(newName, "name must not be null");
        touch();
    }

    /**
     * Assigns the professor that teaches the subject, replacing the professor it had,
     * if any.
     *
     * <p>The course that owns the subject is required because the center of a subject is
     * the center of its course: the aggregate needs it to apply the "the professor
     * teaches it is a professor of the subject's center" rule itself instead of
     * trusting the caller. It must be the course the subject belongs to.
     *
     * @param professor the professor that becomes the teacher, must not be null
     * @param course    the course that owns {@code subject}, must not be null
     * @throws SubjectProfessorInDifferentCenterException if the professor does not
     *         belong to the center that owns the course
     * @throws IllegalArgumentException if the course does not own this subject
     */
    public void assignProfessor(Professor professor, Course course) {
        Objects.requireNonNull(professor, "professor must not be null");
        Objects.requireNonNull(course, "course must not be null");
        if (!course.id().equals(courseId)) {
            throw new IllegalArgumentException("Course with id " + course.id().value()
                    + " does not own subject with id " + id.value());
        }
        if (!course.centerId().equals(professor.centerId())) {
            throw new SubjectProfessorInDifferentCenterException(professor.id(), course.centerId());
        }
        if (!Objects.equals(professor.id(), professorId)) {
            this.professorId = professor.id();
            touch();
        }
    }

    /**
     * Removes the professor that teaches the subject. No-op (idempotent) if the
     * subject has no professor.
     */
    public void unassignProfessor() {
        if (professorId != null) {
            this.professorId = null;
            touch();
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public SubjectId id() {
        return id;
    }

    public SubjectName name() {
        return name;
    }

    public CourseId courseId() {
        return courseId;
    }

    /**
     * Returns the professor that teaches the subject, if it has one.
     *
     * @return the professor identifier, or {@code null} when the subject has no professor
     */
    public ProfessorId professorId() {
        return professorId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
