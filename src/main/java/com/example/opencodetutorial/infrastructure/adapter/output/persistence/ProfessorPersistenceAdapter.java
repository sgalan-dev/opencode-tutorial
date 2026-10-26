package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.exception.DuplicateProfessorInCenterException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link ProfessorRepository}.
 * Owns the only translation between the domain {@link Professor} aggregate and the
 * {@link ProfessorJpaEntity} row representation.
 *
 * <p>The {@code UNIQUE (person_id, center_id)} constraint of the table is the second
 * layer of the "one professor per person per center" rule: a request that slips past
 * the domain check (for instance two concurrent ones) is rejected by the database and
 * translated here into the very same domain conflict.
 */
@Component
public class ProfessorPersistenceAdapter implements ProfessorRepository {

    /**
     * Name of the unique constraint that backs the "one professor per person per center"
     * rule, as the database reports it.
     */
    private static final String PERSON_CENTER_CONSTRAINT = "UK_PROFESSOR_PERSON_CENTER";

    private final ProfessorJpaRepository repository;

    /**
     * Builds the adapter with its required collaborator.
     *
     * @param repository the JPA repository, must not be null
     */
    public ProfessorPersistenceAdapter(ProfessorJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Professor save(Professor professor) {
        ProfessorJpaEntity entity = toEntity(professor);
        try {
            // saveAndFlush so a unique-constraint violation surfaces here and can be
            // translated, instead of blowing up later at transaction commit time.
            ProfessorJpaEntity saved = repository.saveAndFlush(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException ex) {
            if (isPersonCenterUniquenessViolation(ex)) {
                throw new DuplicateProfessorInCenterException(professor.personId(), professor.centerId());
            }
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Professor> findById(ProfessorId id) {
        return repository.findById(id.value()).map(ProfessorPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Professor> findAll() {
        return repository.findAll().stream()
                .map(ProfessorPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Professor> findByCenterId(CenterId centerId) {
        return repository.findByCenterId(centerId.value()).stream()
                .map(ProfessorPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(ProfessorId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ProfessorId id) {
        return repository.existsById(id.value());
    }

    private static boolean isPersonCenterUniquenessViolation(DataIntegrityViolationException ex) {
        Throwable current = ex;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.toUpperCase().contains(PERSON_CENTER_CONSTRAINT)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static ProfessorJpaEntity toEntity(Professor professor) {
        ProfessorJpaEntity entity = new ProfessorJpaEntity();
        if (professor.id() != null) {
            entity.setId(professor.id().value());
        }
        entity.setPersonId(professor.personId().value());
        entity.setCenterId(professor.centerId().value());
        entity.setCreatedAt(professor.createdAt());
        entity.setUpdatedAt(professor.updatedAt());
        return entity;
    }

    private static Professor toDomain(ProfessorJpaEntity entity) {
        return Professor.rehydrate(
                new ProfessorId(entity.getId()),
                new PersonId(entity.getPersonId()),
                new CenterId(entity.getCenterId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
