package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UnassignTutorFromGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.UnassignTutorCommand;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UnassignTutorFromGroupUseCase} implementation. The aggregate's
 * {@code unassignTutor} is idempotent: if the group had no tutor, the operation is a
 * no-op and no exception is raised.
 *
 * <p>The removal targets one specific tutor: the professor named in the URL is the one
 * that has to go. Removing a professor that is not the current tutor leaves the group
 * untouched, which is the idempotency the spec asks for.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UnassignTutorFromGroupService implements UnassignTutorFromGroupUseCase {

    private final GroupRepository groupRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository the group repository, must not be null
     * @param hierarchy       used to validate the hierarchy and load the aggregates
     */
    public UnassignTutorFromGroupService(GroupRepository groupRepository, HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(UnassignTutorCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        GroupId groupId = new GroupId(command.groupId());
        ProfessorId professorId = new ProfessorId(command.professorId());

        Group group = hierarchy.requireGroup(centerId, courseId, groupId);
        hierarchy.requireProfessor(centerId, professorId);

        if (professorId.equals(group.tutorProfessorId())) {
            group.unassignTutor();
            groupRepository.save(group);
        }
    }
}
