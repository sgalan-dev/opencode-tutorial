package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Inbound port for the "get professor by id" use case.
 */
public interface GetProfessorUseCase {

    /**
     * Looks up a professor by the center it belongs to and its own identifier.
     *
     * @param centerId    the center identifier, must not be null
     * @param professorId the professor identifier, must not be null
     * @return the requested professor
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor with that identifier exists under that center
     */
    Professor execute(CenterId centerId, ProfessorId professorId);
}
