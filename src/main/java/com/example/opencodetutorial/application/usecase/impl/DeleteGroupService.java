package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteGroupUseCase;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link DeleteGroupUseCase} implementation. Verifies existence
 * first so the caller receives a meaningful 404 instead of a silent no-op.
 */
@Service
public final class DeleteGroupService implements DeleteGroupUseCase {

    private final GroupRepository groupRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param groupRepository the group repository, must not be null
     */
    public DeleteGroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void execute(GroupId id) {
        if (!groupRepository.existsById(id)) {
            throw new GroupNotFoundException(id);
        }
        groupRepository.deleteById(id);
    }
}
