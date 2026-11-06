package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteGroupUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.port.GroupRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DeleteGroupUseCase} implementation. Verifies the group belongs to the
 * course in the URL and removes every enrolment that targeted it before deleting the
 * group itself, so the junction never keeps rows pointing at a missing group.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DeleteGroupService implements DeleteGroupUseCase {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository   the group repository, must not be null
     * @param studentRepository used to clean up the enrolments, must not be null
     * @param hierarchy         used to validate the center/course/group chain
     */
    public DeleteGroupService(GroupRepository groupRepository,
                              StudentRepository studentRepository,
                              HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(CenterId centerId, CourseId courseId, GroupId groupId) {
        hierarchy.requireGroup(centerId, courseId, groupId);

        for (Student student : studentRepository.findAll()) {
            if (student.isAttachedToGroup(groupId)) {
                student.detachFromGroup(groupId);
                studentRepository.save(student);
            }
        }
        groupRepository.deleteById(groupId);
    }
}