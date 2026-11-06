package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Professor;

import java.util.List;

/**
 * Inbound port for the "list professors of a center" use case.
 */
public interface ListProfessorsUseCase {

    /**
     * Returns every professor belonging to the given center, in no particular order.
     *
     * @param centerId the center identifier, must not be null
     * @return the professors of that center, never null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     */
    List<Professor> execute(CenterId centerId);
}
