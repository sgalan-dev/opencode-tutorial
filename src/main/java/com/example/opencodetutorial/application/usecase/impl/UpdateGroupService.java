package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateGroupCommand;
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
 * Default {@link UpdateGroupUseCase} implementation. Verifies the group belongs to the
 * course in the URL, keeps names unique inside that course and asks the aggregate to
 * rename itself. The group stays in its course: moving it is a different operation.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UpdateGroupService implements UpdateGroupUseCase {

    private final GroupRepository groupRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository the group repository, must not be null
     * @param hierarchy       used to validate the center/course/group chain
     */
    public UpdateGroupService(GroupRepository groupRepository, HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public Group execute(UpdateGroupCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        GroupId groupId = new GroupId(command.id());
        Group existing = hierarchy.requireGroup(centerId, courseId, groupId);

        GroupName newName = new GroupName(command.name());
        boolean takenByOther = groupRepository.findByCourseId(courseId).stream()
                .anyMatch(group -> group.name().equals(newName) && !group.id().equals(groupId));
        if (takenByOther) {
            throw new DuplicateNameException("group", newName.value(), "in course " + courseId.value());
        }

        existing.rename(newName);
        return groupRepository.save(existing);
    }
}