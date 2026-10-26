package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateGroupCommand;
import com.example.opencodetutorial.domain.model.Group;

/**
 * Inbound port for the "update group" use case.
 */
public interface UpdateGroupUseCase {

    /**
     * Replaces the given group with the values from the command, keeping it under the
     * same course.
     *
     * @param command the update payload, must not be null
     * @return the updated group (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group with that identifier exists under the supplied course
     */
    Group execute(UpdateGroupCommand command);
}