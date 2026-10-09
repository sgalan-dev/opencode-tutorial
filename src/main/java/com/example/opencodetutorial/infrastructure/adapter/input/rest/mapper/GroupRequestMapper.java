package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.CreateGroupCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateGroupCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupRequest;

/**
 * Translates {@link GroupRequest} payloads into framework-free application commands.
 */
public final class GroupRequestMapper {

    private GroupRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateGroupCommand} from a request payload.
     *
     * @param request the request payload, must not be null
     * @return the create command
     */
    public static CreateGroupCommand toCreateCommand(GroupRequest request) {
        return new CreateGroupCommand(request.name());
    }

    /**
     * Builds an {@link UpdateGroupCommand} from an identifier and a request payload.
     *
     * @param id      the target group identifier
     * @param request the request payload, must not be null
     * @return the update command
     */
    public static UpdateGroupCommand toUpdateCommand(Long id, GroupRequest request) {
        return new UpdateGroupCommand(id, request.name());
    }
}
