package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetProfessorDetailUseCase;
import com.example.opencodetutorial.application.usecase.view.ProfessorDetailView;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetProfessorDetailUseCase} implementation. Orchestrates the
 * professor, its center and the person behind it. The groups tutored by the professor
 * are not part of the assembled view.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetProfessorDetailService implements GetProfessorDetailUseCase {

    private final PersonRepository personRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param personRepository the person repository, must not be null
     * @param hierarchy        used to validate the center and load the professor
     */
    public GetProfessorDetailService(PersonRepository personRepository, HierarchyQueries hierarchy) {
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfessorDetailView execute(CenterId centerId, ProfessorId professorId) {
        Professor professor = hierarchy.requireProfessor(centerId, professorId);
        Center center = hierarchy.requireCenter(centerId);
        Person person = personRepository.findById(professor.personId())
                .orElseThrow(() -> new PersonNotFoundException(professor.personId()));
        return new ProfessorDetailView(professor, center, person);
    }
}
