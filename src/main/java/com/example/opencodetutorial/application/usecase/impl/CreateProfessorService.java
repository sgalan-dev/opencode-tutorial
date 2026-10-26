package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateProfessorUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateProfessorCommand;
import com.example.opencodetutorial.domain.exception.DuplicateProfessorInCenterException;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;import com.example.opencodetutorial.domain.port.PersonRepository;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CreateProfessorUseCase} implementation. Verifies the center and the
 * referenced person exist and refuses to register the same person twice as a professor
 * of that center.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class CreateProfessorService implements CreateProfessorUseCase {

    private final ProfessorRepository professorRepository;
    private final PersonRepository personRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param professorRepository the professor repository, must not be null
     * @param personRepository   the person repository, must not be null
     * @param hierarchy          used to validate the center
     */
    public CreateProfessorService(ProfessorRepository professorRepository,
                                  PersonRepository personRepository,
                                  HierarchyQueries hierarchy) {
        this.professorRepository = professorRepository;
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public ProfessorId execute(CreateProfessorCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        PersonId personId = new PersonId(command.personId());

        hierarchy.requireCenter(centerId);
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(personId);
        }
        if (isAlreadyProfessorOf(centerId, personId)) {
            throw new DuplicateProfessorInCenterException(personId, centerId);
        }

        Professor professor = Professor.create(personId, centerId);
        return professorRepository.save(professor).id();
    }

    /**
     * Tells whether the person already holds a professorship in the center.
     *
     * @param centerId the center to look at, must not be null
     * @param personId the person to look for, must not be null
     * @return {@code true} when the person is already a professor of that center
     */
    private boolean isAlreadyProfessorOf(CenterId centerId, PersonId personId) {
        return professorRepository.findByCenterId(centerId).stream()
                .anyMatch(professor -> professor.personId().equals(personId));
    }
}
