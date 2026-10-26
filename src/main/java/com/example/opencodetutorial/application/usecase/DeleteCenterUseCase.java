package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;

/**
 * Inbound port for the "delete center" use case.
 */
public interface DeleteCenterUseCase {

    /**
     * Removes the center with the given identifier, refusing while it still owns
     * courses or students.
     *
     * @param id the center identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.ResourceHasDependentsException
     *         if the center still owns courses or students
     */
    void execute(CenterId id);
}