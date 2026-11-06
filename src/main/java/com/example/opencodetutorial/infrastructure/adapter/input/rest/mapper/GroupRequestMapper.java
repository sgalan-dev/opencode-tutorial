package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.CreateGroupCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateGroupCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupRequest;

/**
 * Translates {@link GroupRequest} payloads into framework-free application commands.
 * The parent center and course always come from the canonical URL.
 */
public final class GroupRequestMapper {

    private GroupRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateGroupCommand} from the URL chain and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param courseId the parent course taken from the URL
     * @param request  the request payload, must not be null
     * @return the create command
     */
    public static CreateGroupCommand toCreateCommand(Long centerId, Long courseId, GroupRequest request) {
        return new CreateGroupCommand(centerId, courseId, request.name());
    }

    /**
     * Builds an {@link UpdateGroupCommand} from the URL chain and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param courseId the parent course taken from the URL
     * @param groupId  the target group taken from the URL
     * @param request  the request payload, must not be null
     * @return the update command
     */
    public static UpdateGroupCommand toUpdateCommand(Long centerId, Long courseId, Long groupId,
                                                    GroupRequest request) {
        return new UpdateGroupCommand(centerId, courseId, groupId, request.name());
    }
}