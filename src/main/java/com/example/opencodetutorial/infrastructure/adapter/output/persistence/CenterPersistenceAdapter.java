package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CenterName;
import com.example.opencodetutorial.domain.port.CenterRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link CenterRepository}.
 * Owns the only translation between the domain {@link Center} aggregate and the
 * {@link CenterJpaEntity} row representation.
 */
@Component
public class CenterPersistenceAdapter implements CenterRepository {

    private final CenterJpaRepository repository;

    /**
     * Builds the adapter with its required collaborator.
     *
     * @param repository the JPA repository, must not be null
     */
    public CenterPersistenceAdapter(CenterJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Center save(Center center) {
        CenterJpaEntity entity = toEntity(center);
        CenterJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Center> findById(CenterId id) {
        return repository.findById(id.value()).map(CenterPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Center> findAll() {
        return repository.findAll().stream()
                .map(CenterPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(CenterId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(CenterId id) {
        return repository.existsById(id.value());
    }

    private static CenterJpaEntity toEntity(Center center) {
        CenterJpaEntity entity = new CenterJpaEntity();
        if (center.id() != null) {
            entity.setId(center.id().value());
        }
        entity.setName(center.name().value());
        entity.setCreatedAt(center.createdAt());
        entity.setUpdatedAt(center.updatedAt());
        return entity;
    }

    private static Center toDomain(CenterJpaEntity entity) {
        return Center.rehydrate(
                new CenterId(entity.getId()),
                new CenterName(entity.getName()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}