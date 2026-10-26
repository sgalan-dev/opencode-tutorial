package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.GroupName;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link GroupRepository}.
 * Owns the only translation between the domain {@link Group} aggregate and the
 * {@link GroupJpaEntity} row representation.
 */
@Component
public class GroupPersistenceAdapter implements GroupRepository {

    private final GroupJpaRepository repository;

    /**
     * Builds the adapter with its required collaborator.
     *
     * @param repository the JPA repository, must not be null
     */
    public GroupPersistenceAdapter(GroupJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Group save(Group group) {
        GroupJpaEntity entity = toEntity(group);
        GroupJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findById(GroupId id) {
        return repository.findById(id.value()).map(GroupPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findAll() {
        return repository.findAll().stream()
                .map(GroupPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findByCourseId(CourseId courseId) {
        return repository.findByCourseId(courseId.value()).stream()
                .map(GroupPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findByTutorProfessorId(ProfessorId professorId) {
        return repository.findByTutorProfessorId(professorId.value()).stream()
                .map(GroupPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(GroupId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(GroupId id) {
        return repository.existsById(id.value());
    }

    private static GroupJpaEntity toEntity(Group group) {
        GroupJpaEntity entity = new GroupJpaEntity();
        if (group.id() != null) {
            entity.setId(group.id().value());
        }
        entity.setCourseId(group.courseId().value());
        entity.setTutorProfessorId(group.tutorProfessorId() == null ? null : group.tutorProfessorId().value());
        entity.setName(group.name().value());
        entity.setCreatedAt(group.createdAt());
        entity.setUpdatedAt(group.updatedAt());
        return entity;
    }

    private static Group toDomain(GroupJpaEntity entity) {
        return Group.rehydrate(
                new GroupId(entity.getId()),
                new GroupName(entity.getName()),
                new CourseId(entity.getCourseId()),
                entity.getTutorProfessorId() == null ? null : new ProfessorId(entity.getTutorProfessorId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
