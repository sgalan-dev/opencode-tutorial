package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Group;

import java.util.List;

/**
 * Inbound port for the "list groups" use case.
 */
public interface ListGroupsUseCase {

    /**
     * Returns every persisted group, in no particular order.
     *
     * @return the list of groups, never null
     */
    List<Group> execute();
}
