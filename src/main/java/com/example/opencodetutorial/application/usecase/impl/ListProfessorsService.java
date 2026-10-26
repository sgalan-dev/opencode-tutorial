package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListProfessorsUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link ListProfessorsUseCase} implementation. The parent center must exist,
 * so an unknown center is a 404 instead of an empty list.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class ListProfessorsService implements ListProfessorsUseCase {

    private final ProfessorRepository professorRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param professorRepository the professor repository, must not be null
     * @param hierarchy          used to verify the parent center exists
     */
    public ListProfessorsService(ProfessorRepository professorRepository, HierarchyQueries hierarchy) {
        this.professorRepository = professorRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Professor> execute(CenterId centerId) {
        hierarchy.requireCenter(centerId);
        return professorRepository.findByCenterId(centerId);
    }
}
