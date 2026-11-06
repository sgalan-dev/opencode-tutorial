package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link CourseJpaEntity}.
 * Used only by {@code CoursePersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, Long> {

    /**
     * Lists every course owned by the given center.
     *
     * @param centerId the owning center identifier, must not be null
     * @return the courses of that center, never null
     */
    List<CourseJpaEntity> findByCenterId(Long centerId);
}