package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.CreateCenterCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateCenterCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.CenterRequest;

/**
 * Translates {@link CenterRequest} payloads into framework-free application commands.
 */
public final class CenterRequestMapper {

    private CenterRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateCenterCommand} from a request payload.
     *
     * @param request the request payload, must not be null
     * @return the create command
     */
    public static CreateCenterCommand toCreateCommand(CenterRequest request) {
        return new CreateCenterCommand(request.name());
    }

    /**
     * Builds an {@link UpdateCenterCommand} from an identifier and a request payload.
     *
     * @param id      the target center identifier
     * @param request the request payload, must not be null
     * @return the update command
     */
    public static UpdateCenterCommand toUpdateCommand(Long id, CenterRequest request) {
        return new UpdateCenterCommand(id, request.name());
    }
}