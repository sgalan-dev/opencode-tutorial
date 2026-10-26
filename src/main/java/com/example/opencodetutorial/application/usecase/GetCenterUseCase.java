package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;

/**
 * Inbound port for the "get center by id" use case.
 */
public interface GetCenterUseCase {

    /**
     * Looks up a center by its identifier.
     *
     * @param id the center identifier, must not be null
     * @return the center with the given identifier
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the given identifier
     */
    Center execute(CenterId id);
}