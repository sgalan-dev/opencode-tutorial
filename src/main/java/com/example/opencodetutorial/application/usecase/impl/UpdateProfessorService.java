package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateProfessorUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateProfessorCommand;
import com.example.opencodetutorial.domain.exception.DuplicateProfessorInCenterException;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.PersonRepository;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UpdateProfessorUseCase} implementation. Replaces the person
 * associated with the professor, keeping the center untouched, and refuses to leave two
 * professorships of the same person inside the same center.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UpdateProfessorService implements UpdateProfessorUseCase {

    private final ProfessorRepository professorRepository;
    private final PersonRepository personRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param professorRepository the professor repository, must not be null
     * @param personRepository   the person repository, must not be null
     * @param hierarchy          used to validate the center and load the professor
     */
    public UpdateProfessorService(ProfessorRepository professorRepository,
                                  PersonRepository personRepository,
                                  HierarchyQueries hierarchy) {
        this.professorRepository = professorRepository;
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public Professor execute(UpdateProfessorCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        ProfessorId professorId = new ProfessorId(command.id());
        Professor existing = hierarchy.requireProfessor(centerId, professorId);

        PersonId personId = new PersonId(command.personId());
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(personId);
        }
        boolean takenByOther = professorRepository.findByCenterId(centerId).stream()
                .anyMatch(professor -> professor.personId().equals(personId)
                        && !professor.id().equals(professorId));
        if (takenByOther) {
            throw new DuplicateProfessorInCenterException(personId, centerId);
        }

        existing.changePerson(personId);
        return professorRepository.save(existing);
    }
}
