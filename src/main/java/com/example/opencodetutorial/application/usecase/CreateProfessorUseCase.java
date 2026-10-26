package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateProfessorCommand;
import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Inbound port for the "create professor" use case.
 */
public interface CreateProfessorUseCase {

    /**
     * Creates a new professor inside the given center and returns the identifier
     * assigned by the persistence layer.
     *
     * @param command the data required to create the professor, must not be null
     * @return the identifier of the newly created professor
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.DuplicateProfessorInCenterException
     *         if the person is already a professor of that center
     */
    ProfessorId execute(CreateProfessorCommand command);
}
