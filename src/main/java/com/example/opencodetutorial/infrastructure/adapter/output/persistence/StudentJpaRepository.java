package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link StudentJpaEntity}.
 * Used only by {@code StudentPersistenceAdapter}; never injected directly into
 * the application or domain layers.
 */
public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {
}
