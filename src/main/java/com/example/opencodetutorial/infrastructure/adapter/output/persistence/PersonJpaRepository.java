package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link PersonJpaEntity}.
 * Used only by {@code PersonPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface PersonJpaRepository extends JpaRepository<PersonJpaEntity, Long> {
}
