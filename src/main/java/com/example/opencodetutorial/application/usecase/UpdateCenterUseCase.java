package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateCenterCommand;
import com.example.opencodetutorial.domain.model.Center;

/**
 * Inbound port for the "update center" use case.
 */
public interface UpdateCenterUseCase {

    /**
     * Replaces the given center with the values from the command.
     *
     * @param command the update payload, must not be null
     * @return the updated center (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.DuplicateNameException
     *         if another center already uses the supplied name
     */
    Center execute(UpdateCenterCommand command);
}