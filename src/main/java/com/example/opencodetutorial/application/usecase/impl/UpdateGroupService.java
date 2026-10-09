package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateGroupCommand;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.GroupName;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link UpdateGroupUseCase} implementation. Loads the existing
 * aggregate, asks it to apply the new state through its behaviour methods,
 * and hands it back to the repository.
 */
@Service
public final class UpdateGroupService implements UpdateGroupUseCase {

    private final GroupRepository groupRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param groupRepository the group repository, must not be null
     */
    public UpdateGroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group execute(UpdateGroupCommand command) {
        GroupId id = new GroupId(command.id());
        Group existing = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));

        existing.rename(new GroupName(command.name()));
        return groupRepository.save(existing);
    }
}
