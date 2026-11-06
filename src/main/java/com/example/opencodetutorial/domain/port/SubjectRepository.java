package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Subject} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that subjects are
 * stored as JPA entities; the domain layer sees only this abstraction.
 */
public interface SubjectRepository {

    /**
     * Persists a subject, inserting a new aggregate or updating an existing one. The
     * professor that teaches the subject, if any, is persisted with it.
     *
     * @param subject the subject to save, must not be null
     * @return the saved subject (with its identifier assigned if it was new)
     */
    Subject save(Subject subject);

    /**
     * Looks up a subject by its identifier.
     *
     * @param id the subject identifier, must not be null
     * @return an {@link Optional} containing the subject if found, otherwise empty
     */
    Optional<Subject> findById(SubjectId id);

    /**
     * Lists every persisted subject, in no particular order.
     *
     * @return the list of subjects, never null
     */
    List<Subject> findAll();

    /**
     * Lists the subjects that belong to the given course.
     *
     * @param courseId the owning course identifier, must not be null
     * @return the subjects of that course, never null (empty when it has none)
     */
    List<Subject> findByCourseId(CourseId courseId);

    /**
     * Lists the subjects taught by the given professor.
     *
     * <p>This is the inverse of the teaching reference and it is NOT exposed through the
     * API: it exists only as the guard that refuses deleting a professor who still
     * teaches at least one subject.
     *
     * @param professorId the teacher identifier, must not be null
     * @return the subjects taught by that professor, never null (empty when it has none)
     */
    List<Subject> findByProfessorId(ProfessorId professorId);

    /**
     * Removes the subject with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the subject identifier, must not be null
     */
    void deleteById(SubjectId id);

    /**
     * Checks whether a subject with the given identifier exists.
     *
     * @param id the subject identifier, must not be null
     * @return {@code true} if the subject exists, {@code false} otherwise
     */
    boolean existsById(SubjectId id);
}
