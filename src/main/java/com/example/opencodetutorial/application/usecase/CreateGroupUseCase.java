package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateGroupCommand;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the "create group" use case.
 */
public interface CreateGroupUseCase {

    /**
     * Creates a new group from the supplied command and returns the identifier
     * assigned by the persistence layer.
     *
     * @param command the data required to create the group, must not be null
     * @return the identifier of the newly created group
     */
    GroupId execute(CreateGroupCommand command);
}
