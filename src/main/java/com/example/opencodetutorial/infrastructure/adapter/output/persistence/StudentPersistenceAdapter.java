package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.exception.StudentAlreadyEnrolledInCourseException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA-backed implementation of {@link StudentRepository}.
 * Owns the only translation between the domain {@link Student} aggregate and
 * the {@link StudentJpaEntity} row representation, including the mapping of
 * the many-to-many enrolments to the {@code student_group_link} junction managed
 * as an {@code @ElementCollection}.
 *
 * <p>The junction stores the denormalized course of every link so the database can
 * enforce {@code UNIQUE (student_id, course_id)}. When the aggregate does not know the
 * course of a group (for example right after {@code Student.create}), the adapter
 * resolves it from the {@code student_group} table.
 */
@Component
public class StudentPersistenceAdapter implements StudentRepository {

    private final StudentJpaRepository repository;
    private final GroupJpaRepository groupRepository;

    /**
     * Builds the adapter with its required collaborators.
     *
     * @param repository      the JPA repository, must not be null
     * @param groupRepository used to resolve the denormalized course of each link,
     *                        must not be null
     */
    public StudentPersistenceAdapter(StudentJpaRepository repository,
                                     GroupJpaRepository groupRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public Student save(Student student) {
        StudentJpaEntity entity = toEntity(student);
        try {
            // saveAndFlush so a unique-constraint violation surfaces here and can be
            // translated, instead of blowing up later at transaction commit time.
            StudentJpaEntity saved = repository.saveAndFlush(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            if (isCourseUniquenessViolation(ex)) {
                throw new StudentAlreadyEnrolledInCourseException(student.id(), conflictingCourse(student));
            }
            throw ex;
        }
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
    @Transactional(readOnly = true)
    public List<Student> findByCenterId(CenterId centerId) {
        return repository.findByCenterId(centerId.value()).stream()
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

    private StudentJpaEntity toEntity(Student student) {
        StudentJpaEntity entity = new StudentJpaEntity();
        if (student.id() != null) {
            entity.setId(student.id().value());
        }
        entity.setPersonId(student.personId().value());
        entity.setCenterId(student.centerId().value());
        // Replace the collection contents in place so JPA tracks the diff and
        // emits the correct INSERT/DELETE statements for the junction rows.
        entity.getEnrolments().clear();
        entity.getEnrolments().addAll(toLinks(student));
        entity.setCreatedAt(student.createdAt());
        entity.setUpdatedAt(student.updatedAt());
        return entity;
    }

    private List<EnrollmentLinkJpa> toLinks(Student student) {
        Map<GroupId, CourseId> enrolments = student.enrolments();
        Map<Long, Long> courseByGroup = resolveUnknownCourses(enrolments);
        List<EnrollmentLinkJpa> links = new ArrayList<>(enrolments.size());
        enrolments.forEach((groupId, courseId) -> {
            Long groupValue = groupId.value();
            Long courseValue = courseId != null ? courseId.value() : courseByGroup.get(groupValue);
            if (courseValue == null) {
                throw new IllegalStateException(
                        "No course found for group " + groupValue + "; the enrolment cannot be stored");
            }
            links.add(new EnrollmentLinkJpa(groupValue, courseValue));
        });
        return List.copyOf(links);
    }

    private Map<Long, Long> resolveUnknownCourses(Map<GroupId, CourseId> enrolments) {
        Set<Long> pending = enrolments.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(entry -> entry.getKey().value())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (pending.isEmpty()) {
            return Map.of();
        }
        return groupRepository.findByIdIn(pending).stream()
                .collect(Collectors.toMap(
                        GroupJpaEntity::getId,
                        GroupJpaEntity::getCourseId,
                        (first, second) -> first,
                        LinkedHashMap::new));
    }

    private static CourseId conflictingCourse(Student student) {
        Set<CourseId> distinctCourses = new LinkedHashSet<>(student.enrolments().values());
        return distinctCourses.size() == 1 ? distinctCourses.iterator().next() : null;
    }

    private static boolean isCourseUniquenessViolation(DataIntegrityViolationException ex) {
        return messageChainContains(ex, "UK_STUDENT_COURSE");
    }

    private static boolean messageChainContains(Throwable throwable, String token) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.toUpperCase().contains(token)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static Student toDomain(StudentJpaEntity entity) {
        Map<GroupId, CourseId> enrolments = entity.getEnrolments().stream()
                .collect(Collectors.toMap(
                        link -> new GroupId(Objects.requireNonNull(link.getGroupId())),
                        link -> new CourseId(Objects.requireNonNull(link.getCourseId())),
                        (first, second) -> first,
                        LinkedHashMap::new));
        return Student.rehydrate(
                new StudentId(entity.getId()),
                new PersonId(entity.getPersonId()),
                new CenterId(entity.getCenterId()),
                enrolments,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}