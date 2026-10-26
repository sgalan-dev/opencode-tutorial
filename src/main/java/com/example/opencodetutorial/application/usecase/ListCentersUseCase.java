package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Center;

import java.util.List;

/**
 * Inbound port for the "list centers" use case.
 */
public interface ListCentersUseCase {

    /**
     * Returns every persisted center, in no particular order.
     *
     * @return the list of centers, never null
     */
    List<Center> execute();
}