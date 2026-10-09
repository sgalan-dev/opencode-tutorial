package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreatePersonCommand;
import com.example.opencodetutorial.domain.model.PersonId;

/**
 * Inbound port for the "create person" use case.
 * The application layer provides the implementation; the infrastructure layer
 * (typically a REST controller) is the only caller allowed to invoke it.
 */
public interface CreatePersonUseCase {

    /**
     * Creates a new person from the supplied command and returns the identifier
     * assigned by the persistence layer.
     *
     * @param command the data required to create the person, must not be null
     * @return the identifier of the newly created person
     */
    PersonId execute(CreatePersonCommand command);
}
