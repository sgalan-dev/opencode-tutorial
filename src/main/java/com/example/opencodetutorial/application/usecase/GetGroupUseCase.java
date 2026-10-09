package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the "get group by id" use case.
 */
public interface GetGroupUseCase {

    /**
     * Looks up a group by its identifier.
     *
     * @param id the group identifier, must not be null
     * @return the group with the given identifier
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the given identifier
     */
    Group execute(GroupId id);
}
