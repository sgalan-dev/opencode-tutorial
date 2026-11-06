package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Inbound port for the "delete professor" use case.
 */
public interface DeleteProfessorUseCase {

    /**
     * Removes the professor with the given identifiers. Refused while the professor
     * still tutors at least one group.
     *
     * @param centerId    the center identifier, must not be null
     * @param professorId the professor identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.ResourceHasDependentsException
     *         if the professor is still the tutor of at least one group
     */
    void execute(CenterId centerId, ProfessorId professorId);
}
