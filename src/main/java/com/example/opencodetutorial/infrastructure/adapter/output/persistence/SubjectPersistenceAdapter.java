package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.domain.model.SubjectName;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link SubjectRepository}.
 * Owns the only translation between the domain {@link Subject} aggregate and the
 * {@link SubjectJpaEntity} row representation, including the split of the aggregate
 * across the {@code subject} and {@code subject_teaching} tables: the subject row
 * carries the name and the course, the teaching row carries the professor that
 * teaches it.
 */
@Component
public class SubjectPersistenceAdapter implements SubjectRepository {

    /**
     * Name of the unique constraint that backs the "unique name within the course" rule.
     */
    private static final String SUBJECT_NAME_CONSTRAINT = "UK_SUBJECT_COURSE_NAME";

    private final SubjectJpaRepository repository;
    private final SubjectTeachingJpaRepository teachingRepository;

    /**
     * Builds the adapter with its required collaborators.
     *
     * @param repository         the JPA repository of the subjects, must not be null
     * @param teachingRepository the JPA repository of the teaching assignments,
     *                           must not be null
     */
    public SubjectPersistenceAdapter(SubjectJpaRepository repository,
                                     SubjectTeachingJpaRepository teachingRepository) {
        this.repository = repository;
        this.teachingRepository = teachingRepository;
    }

    @Override
    @Transactional
    public Subject save(Subject subject) {
        SubjectJpaEntity entity = toEntity(subject);
        SubjectJpaEntity saved;
        try {
            // saveAndFlush so a unique-constraint violation surfaces here and can be
            // translated, instead of blowing up later at transaction commit time.
            saved = repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ex) {
            if (isSubjectNameUniquenessViolation(ex)) {
                throw new DuplicateNameException("subject", subject.name().value(),
                        "in course " + subject.courseId().value());
            }
            throw ex;
        }
        Long professorId = subject.professorId() == null ? null : subject.professorId().value();
        syncTeaching(saved, professorId);
        return toDomain(saved, professorId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subject> findById(SubjectId id) {
        return repository.findById(id.value())
                .map(entity -> toDomain(entity, professorOf(entity.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> findAll() {
        Map<Long, Long> professorsBySubject = professorsBySubject(teachingRepository.findAll());
        return repository.findAll().stream()
                .map(entity -> toDomain(entity, professorsBySubject.get(entity.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByCourseId(CourseId courseId) {
        // The teaching rows of the course are loaded in one go and paired in memory,
        // so listing the subjects of a course never queries once per subject.
        Map<Long, Long> professorsBySubject = professorsBySubject(teachingRepository.findByCourseId(courseId.value()));
        return repository.findByCourseId(courseId.value()).stream()
                .map(entity -> toDomain(entity, professorsBySubject.get(entity.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByProfessorId(ProfessorId professorId) {
        List<SubjectTeachingJpaEntity> rows = teachingRepository.findByProfessorId(professorId.value());
        if (rows.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> professorsBySubject = professorsBySubject(rows);
        List<Long> subjectIds = rows.stream()
                .map(SubjectTeachingJpaEntity::getSubjectId)
                .toList();
        return repository.findAllById(subjectIds).stream()
                .map(entity -> toDomain(entity, professorsBySubject.get(entity.getId())))
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(SubjectId id) {
        teachingRepository.deleteBySubjectId(id.value());
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(SubjectId id) {
        return repository.existsById(id.value());
    }

    /**
     * Writes or removes the teaching row that matches the professor of the aggregate.
     *
     * @param saved       the persisted subject row, must not be null
     * @param professorId the professor that teaches the subject, or {@code null} when
     *                    it has none
     */
    private void syncTeaching(SubjectJpaEntity saved, Long professorId) {
        Long subjectId = saved.getId();
        Optional<SubjectTeachingJpaEntity> existing = teachingRepository.findBySubjectId(subjectId);
        if (professorId == null) {
            existing.ifPresent(teachingRepository::delete);
            return;
        }
        SubjectTeachingJpaEntity row = existing.orElseGet(() -> {
            SubjectTeachingJpaEntity created = new SubjectTeachingJpaEntity();
            created.setSubjectId(subjectId);
            return created;
        });
        row.setCourseId(saved.getCourseId());
        row.setProfessorId(professorId);
        teachingRepository.save(row);
    }

    /**
     * Returns the professor that teaches the given subject, if it has one.
     *
     * @param subjectId the subject identifier, must not be null
     * @return the professor identifier, or {@code null} when the subject has none
     */
    private Long professorOf(Long subjectId) {
        return teachingRepository.findBySubjectId(subjectId)
                .map(SubjectTeachingJpaEntity::getProfessorId)
                .orElse(null);
    }

    /**
     * Pairs every teaching row with the identifier of the subject it belongs to.
     *
     * @param rows the teaching rows, must not be null
     * @return the professor identifier of every subject that has one
     */
    private static Map<Long, Long> professorsBySubject(List<SubjectTeachingJpaEntity> rows) {
        Map<Long, Long> professors = new LinkedHashMap<>();
        rows.forEach(row -> professors.put(row.getSubjectId(), row.getProfessorId()));
        return professors;
    }

    private static SubjectJpaEntity toEntity(Subject subject) {
        SubjectJpaEntity entity = new SubjectJpaEntity();
        if (subject.id() != null) {
            entity.setId(subject.id().value());
        }
        entity.setCourseId(subject.courseId().value());
        entity.setName(subject.name().value());
        entity.setCreatedAt(subject.createdAt());
        entity.setUpdatedAt(subject.updatedAt());
        return entity;
    }

    private static Subject toDomain(SubjectJpaEntity entity, Long professorId) {
        return Subject.rehydrate(
                new SubjectId(entity.getId()),
                new SubjectName(entity.getName()),
                new CourseId(entity.getCourseId()),
                professorId == null ? null : new ProfessorId(professorId),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static boolean isSubjectNameUniquenessViolation(DataIntegrityViolationException ex) {
        return messageChainContains(ex, SUBJECT_NAME_CONSTRAINT);
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
}
