package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link CenterJpaEntity}.
 * Used only by {@code CenterPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface CenterJpaRepository extends JpaRepository<CenterJpaEntity, Long> {
}