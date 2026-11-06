package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateGroupCommand;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the "create group" use case.
 */
public interface CreateGroupUseCase {

    /**
     * Creates a new group inside the given course of the given center and returns the
     * identifier assigned by the persistence layer.
     *
     * @param command the data required to create the group, must not be null
     * @return the identifier of the newly created group
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with the supplied identifier exists under that center
     */
    GroupId execute(CreateGroupCommand command);
}