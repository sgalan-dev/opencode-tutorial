package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubjectTeachingJpaEntity}.
 * Used only by {@code SubjectPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface SubjectTeachingJpaRepository extends JpaRepository<SubjectTeachingJpaEntity, Long> {

    /**
     * Loads the teaching row of the given subject.
     *
     * @param subjectId the subject identifier, must not be null
     * @return the teaching row, if the subject has a professor
     */
    Optional<SubjectTeachingJpaEntity> findBySubjectId(Long subjectId);

    /**
     * Lists every teaching row of the given course. Used to rehydrate the professor of
     * a whole course of subjects with a single query.
     *
     * @param courseId the owning course identifier, must not be null
     * @return the matching rows, never null
     */
    List<SubjectTeachingJpaEntity> findByCourseId(Long courseId);

    /**
     * Lists every teaching row of the given professor. Used by the application layer
     * as the guard that refuses deleting a professor who still teaches a subject.
     *
     * @param professorId the teacher identifier, must not be null
     * @return the matching rows, never null
     */
    List<SubjectTeachingJpaEntity> findByProfessorId(Long professorId);

    /**
     * Removes the teaching row of the given subject, if any.
     *
     * @param subjectId the subject identifier, must not be null
     */
    void deleteBySubjectId(Long subjectId);
}
