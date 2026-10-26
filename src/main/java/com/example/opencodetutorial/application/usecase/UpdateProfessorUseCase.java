package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateProfessorCommand;
import com.example.opencodetutorial.domain.model.Professor;

/**
 * Inbound port for the "update professor" use case.
 */
public interface UpdateProfessorUseCase {

    /**
     * Replaces the person associated with the professor with the one from the command.
     * The center of the professor never changes.
     *
     * @param command the update payload, must not be null
     * @return the updated professor (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.DuplicateProfessorInCenterException
     *         if the new person is already a professor of that center
     */
    Professor execute(UpdateProfessorCommand command);
}
