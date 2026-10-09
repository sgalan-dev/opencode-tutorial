package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListGroupsUseCase;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default {@link ListGroupsUseCase} implementation.
 */
@Service
public final class ListGroupsService implements ListGroupsUseCase {

    private final GroupRepository groupRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param groupRepository the group repository, must not be null
     */
    public ListGroupsService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public List<Group> execute() {
        return groupRepository.findAll();
    }
}
