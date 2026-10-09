package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.PersonId;

/**
 * Inbound port for the "delete person" use case.
 */
public interface DeletePersonUseCase {

    /**
     * Removes the person with the given identifier.
     *
     * @param id the person identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the supplied identifier
     */
    void execute(PersonId id);
}
