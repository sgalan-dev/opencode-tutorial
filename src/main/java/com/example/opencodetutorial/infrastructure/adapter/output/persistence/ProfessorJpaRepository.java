package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ProfessorJpaEntity}.
 * Used only by {@code ProfessorPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface ProfessorJpaRepository extends JpaRepository<ProfessorJpaEntity, Long> {

    /**
     * Lists every professor that belongs to the given center.
     *
     * @param centerId the owning center identifier, must not be null
     * @return the professors of that center, never null
     */
    List<ProfessorJpaEntity> findByCenterId(Long centerId);
}
