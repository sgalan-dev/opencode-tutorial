package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.CourseName;
import com.example.opencodetutorial.domain.port.CourseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link CourseRepository}.
 * Owns the only translation between the domain {@link Course} aggregate and the
 * {@link CourseJpaEntity} row representation, including the filtering by owning
 * center, which is pushed down to the database instead of being done in memory.
 */
@Component
public class CoursePersistenceAdapter implements CourseRepository {

    private final CourseJpaRepository repository;

    /**
     * Builds the adapter with its required collaborator.
     *
     * @param repository the JPA repository, must not be null
     */
    public CoursePersistenceAdapter(CourseJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Course save(Course course) {
        CourseJpaEntity entity = toEntity(course);
        CourseJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findById(CourseId id) {
        return repository.findById(id.value()).map(CoursePersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return repository.findAll().stream()
                .map(CoursePersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findByCenterId(CenterId centerId) {
        return repository.findByCenterId(centerId.value()).stream()
                .map(CoursePersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(CourseId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(CourseId id) {
        return repository.existsById(id.value());
    }

    private static CourseJpaEntity toEntity(Course course) {
        CourseJpaEntity entity = new CourseJpaEntity();
        if (course.id() != null) {
            entity.setId(course.id().value());
        }
        entity.setCenterId(course.centerId().value());
        entity.setName(course.name().value());
        entity.setCreatedAt(course.createdAt());
        entity.setUpdatedAt(course.updatedAt());
        return entity;
    }

    private static Course toDomain(CourseJpaEntity entity) {
        return Course.rehydrate(
                new CourseId(entity.getId()),
                new CourseName(entity.getName()),
                new CenterId(entity.getCenterId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}