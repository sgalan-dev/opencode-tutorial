package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateCenterCommand;
import com.example.opencodetutorial.domain.model.CenterId;

/**
 * Inbound port for the "create center" use case.
 */
public interface CreateCenterUseCase {

    /**
     * Creates a new center from the supplied command and returns the identifier
     * assigned by the persistence layer.
     *
     * @param command the data required to create the center, must not be null
     * @return the identifier of the newly created center
     * @throws com.example.opencodetutorial.domain.exception.DuplicateNameException
     *         if another center already uses the supplied name
     */
    CenterId execute(CreateCenterCommand command);
}