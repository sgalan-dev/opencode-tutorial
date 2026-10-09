package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the "delete group" use case.
 */
public interface DeleteGroupUseCase {

    /**
     * Removes the group with the given identifier.
     *
     * @param id the group identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier
     */
    void execute(GroupId id);
}
