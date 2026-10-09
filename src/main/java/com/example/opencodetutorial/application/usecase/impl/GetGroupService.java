package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetGroupUseCase;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link GetGroupUseCase} implementation. Surfaces a missing group as a
 * domain exception so the REST layer can map it to HTTP 404.
 */
@Service
public final class GetGroupService implements GetGroupUseCase {

    private final GroupRepository groupRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param groupRepository the group repository, must not be null
     */
    public GetGroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group execute(GroupId id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(id));
    }
}
