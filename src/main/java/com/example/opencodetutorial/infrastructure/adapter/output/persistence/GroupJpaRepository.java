package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link GroupJpaEntity}.
 * Used only by {@code GroupPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface GroupJpaRepository extends JpaRepository<GroupJpaEntity, Long> {
}
