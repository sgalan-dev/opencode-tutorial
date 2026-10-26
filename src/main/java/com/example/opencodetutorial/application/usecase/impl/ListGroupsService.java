package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListGroupsUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link ListGroupsUseCase} implementation. The parent course must exist and
 * belong to the supplied center, so an unknown link in the chain is a 404.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class ListGroupsService implements ListGroupsUseCase {

    private final GroupRepository groupRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository the group repository, must not be null
     * @param hierarchy       used to validate the center/course chain
     */
    public ListGroupsService(GroupRepository groupRepository, HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> execute(CenterId centerId, CourseId courseId) {
        hierarchy.requireCourse(centerId, courseId);
        return groupRepository.findByCourseId(courseId);
    }
}