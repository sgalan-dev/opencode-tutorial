package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateGroupCommand;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.GroupName;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CreateGroupUseCase} implementation. Verifies the whole
 * {@code center -> course} chain from the URL, keeps group names unique inside their
 * course and hands the new aggregate to the repository.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class CreateGroupService implements CreateGroupUseCase {

    private final GroupRepository groupRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository the group repository, must not be null
     * @param hierarchy       used to validate the center/course chain
     */
    public CreateGroupService(GroupRepository groupRepository, HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public GroupId execute(CreateGroupCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        hierarchy.requireCourse(centerId, courseId);

        GroupName name = new GroupName(command.name());
        boolean taken = groupRepository.findByCourseId(courseId).stream()
                .anyMatch(group -> group.name().equals(name));
        if (taken) {
            throw new DuplicateNameException("group", name.value(), "in course " + courseId.value());
        }
        Group group = Group.create(name, courseId);
        return groupRepository.save(group).id();
    }
}