package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdatePersonCommand;
import com.example.opencodetutorial.domain.model.Person;

/**
 * Inbound port for the "update person" use case.
 */
public interface UpdatePersonUseCase {

    /**
     * Replaces the given person with the values from the command.
     *
     * @param command the update payload, must not be null
     * @return the updated person (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the supplied identifier
     */
    Person execute(UpdatePersonCommand command);
}
