package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.CreatePersonCommand;
import com.example.opencodetutorial.application.usecase.command.UpdatePersonCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.PersonRequest;

/**
 * Translates {@link PersonRequest} payloads into framework-free application commands.
 * Lives in the adapter boundary so {@code jakarta.validation} stays out of the
 * application layer.
 */
public final class PersonRequestMapper {

    private PersonRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreatePersonCommand} from a request payload.
     *
     * @param request the request payload, must not be null
     * @return the create command
     */
    public static CreatePersonCommand toCreateCommand(PersonRequest request) {
        return new CreatePersonCommand(request.name(), request.surname(), request.surname2());
    }

    /**
     * Builds an {@link UpdatePersonCommand} from an identifier and a request payload.
     *
     * @param id      the target person identifier
     * @param request the request payload, must not be null
     * @return the update command
     */
    public static UpdatePersonCommand toUpdateCommand(Long id, PersonRequest request) {
        return new UpdatePersonCommand(id, request.name(), request.surname(), request.surname2());
    }
}
