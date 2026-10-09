package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;

/**
 * Inbound port for the "get person by id" use case.
 */
public interface GetPersonUseCase {

    /**
     * Looks up a person by its identifier.
     *
     * @param id the person identifier, must not be null
     * @return the person with the given identifier
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the given identifier
     */
    Person execute(PersonId id);
}
