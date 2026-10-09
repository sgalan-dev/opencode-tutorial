package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateGroupCommand;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.GroupName;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link CreateGroupUseCase} implementation.
 */
@Service
public final class CreateGroupService implements CreateGroupUseCase {

    private final GroupRepository groupRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param groupRepository the group repository, must not be null
     */
    public CreateGroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public GroupId execute(CreateGroupCommand command) {
        GroupName name = new GroupName(command.name());
        Group group = Group.create(name);
        return groupRepository.save(group).id();
    }
}
