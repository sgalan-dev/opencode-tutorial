package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link StudentJpaEntity}.
 * Used only by {@code StudentPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    /**
     * Lists every student that belongs to the given center.
     *
     * @param centerId the owning center identifier, must not be null
     * @return the students of that center, never null
     */
    List<StudentJpaEntity> findByCenterId(Long centerId);
}