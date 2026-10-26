package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetGroupUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetGroupUseCase} implementation. A group that belongs to another
 * course is reported as not found, because it is not reachable under this URL.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetGroupService implements GetGroupUseCase {

    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborator.
     *
     * @param hierarchy used to resolve and validate the center/course/group chain
     */
    public GetGroupService(HierarchyQueries hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public Group execute(CenterId centerId, CourseId courseId, GroupId groupId) {
        return hierarchy.requireGroup(centerId, courseId, groupId);
    }
}