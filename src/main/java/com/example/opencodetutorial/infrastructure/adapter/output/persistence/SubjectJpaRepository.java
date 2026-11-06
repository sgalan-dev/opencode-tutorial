package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link SubjectJpaEntity}.
 * Used only by {@code SubjectPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, Long> {

    /**
     * Lists every subject that belongs to the given course.
     *
     * @param courseId the owning course identifier, must not be null
     * @return the subjects of that course, never null
     */
    List<SubjectJpaEntity> findByCourseId(Long courseId);
}
