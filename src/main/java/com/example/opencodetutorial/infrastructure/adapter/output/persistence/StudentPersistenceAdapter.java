package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA-backed implementation of {@link StudentRepository}.
 * Owns the only translation between the domain {@link Student} aggregate and
 * the {@link StudentJpaEntity} row representation, including the mapping of
 * the many-to-many {@code Set<GroupId>} to the {@code student_group_link}
 * junction managed as an {@code @ElementCollection}.
 */
@Component
public class StudentPersistenceAdapter implements StudentRepository {

    private final StudentJpaRepository repository;

    /**
     * Builds the adapter with its required collaborator.
     *
     * @param repository the JPA repository, must not be null
     */
    public StudentPersistenceAdapter(StudentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Student save(Student student) {
        StudentJpaEntity entity = toEntity(student);
        StudentJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(StudentId id) {
        return repository.findById(id.value()).map(StudentPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return repository.findAll().stream()
                .map(StudentPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(StudentId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(StudentId id) {
        return repository.existsById(id.value());
    }

    private static StudentJpaEntity toEntity(Student student) {
        StudentJpaEntity entity = new StudentJpaEntity();
        if (student.id() != null) {
            entity.setId(student.id().value());
        }
        entity.setPersonId(student.personId().value());
        // Replace the collection contents in place so JPA tracks the diff and
        // emits the correct INSERT/DELETE statements for the junction rows.
        Set<Long> rawIds = student.groupIds().stream()
                .map(GroupId::value)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        entity.getGroupIds().clear();
        entity.getGroupIds().addAll(rawIds);
        entity.setCreatedAt(student.createdAt());
        entity.setUpdatedAt(student.updatedAt());
        return entity;
    }

    private static Student toDomain(StudentJpaEntity entity) {
        Set<GroupId> groupIds = entity.getGroupIds().stream()
                .map(GroupId::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Student.rehydrate(
                new StudentId(entity.getId()),
                new PersonId(entity.getPersonId()),
                groupIds,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
