package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA repository for {@link GroupJpaEntity}.
 * Used only by {@code GroupPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, Long> {

    /**
     * Lists every group that belongs to the given course.
     *
     * @param courseId the owning course identifier, must not be null
     * @return the groups of that course, never null
     */
    List<GroupJpaEntity> findByCourseId(Long courseId);

    /**
     * Loads the given groups so callers can read their denormalized course.
     * Used by {@code StudentPersistenceAdapter} to fill {@code course_id} in the
     * enrolment junction.
     *
     * @param ids the group identifiers to load, must not be null
     * @return the matching rows, never null
     */
    List<GroupJpaEntity> findByIdIn(Collection<Long> ids);
}