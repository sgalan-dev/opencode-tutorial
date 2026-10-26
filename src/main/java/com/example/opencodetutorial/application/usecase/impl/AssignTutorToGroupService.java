package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.AssignTutorToGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.AssignTutorCommand;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link AssignTutorToGroupUseCase} implementation.
 *
 * <p>Resolves the full {@code center -> course -> group} chain and the professor
 * scoped to the center of the URL, then delegates to the aggregate's
 * {@code assignTutor(Professor, Course)}, which owns the business rule: the tutor is a
 * professor of the center that owns the group's course. The rule is mirrored by the
 * foreign key of the {@code student_group.tutor_professor_id} column.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class AssignTutorToGroupService implements AssignTutorToGroupUseCase {

    private final GroupRepository groupRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository the group repository, must not be null
     * @param hierarchy       used to validate the hierarchy and load the aggregates
     */
    public AssignTutorToGroupService(GroupRepository groupRepository, HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(AssignTutorCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        GroupId groupId = new GroupId(command.groupId());
        ProfessorId professorId = new ProfessorId(command.professorId());

        Course course = hierarchy.requireCourse(centerId, courseId);
        Group group = hierarchy.requireGroup(centerId, courseId, groupId);
        Professor professor = hierarchy.requireProfessor(centerId, professorId);

        group.assignTutor(professor, course);
        groupRepository.save(group);
    }
}
