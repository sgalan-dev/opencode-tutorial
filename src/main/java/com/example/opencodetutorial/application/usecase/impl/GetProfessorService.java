package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetProfessorUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetProfessorUseCase} implementation. A professor that belongs to
 * another center is reported as not found, because it is not reachable under this URL.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetProfessorService implements GetProfessorUseCase {

    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborator.
     *
     * @param hierarchy used to validate the center and load the professor
     */
    public GetProfessorService(HierarchyQueries hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public Professor execute(CenterId centerId, ProfessorId professorId) {
        return hierarchy.requireProfessor(centerId, professorId);
    }
}
