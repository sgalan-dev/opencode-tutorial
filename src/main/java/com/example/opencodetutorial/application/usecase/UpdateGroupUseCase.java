package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateGroupCommand;
import com.example.opencodetutorial.domain.model.Group;

/**
 * Inbound port for the "update group" use case.
 */
public interface UpdateGroupUseCase {

    /**
     * Replaces the given group with the values from the command.
     *
     * @param command the update payload, must not be null
     * @return the updated group (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier
     */
    Group execute(UpdateGroupCommand command);
}
