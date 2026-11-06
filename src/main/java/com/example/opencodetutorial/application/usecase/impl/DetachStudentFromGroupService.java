package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DetachStudentFromGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.DetachStudentFromGroupCommand;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DetachStudentFromGroupUseCase} implementation. The aggregate's
 * {@code detachFromGroup} is idempotent: if the student was not enrolled, the operation
 * is a no-op and no exception is raised.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DetachStudentFromGroupService implements DetachStudentFromGroupUseCase {

    private final StudentRepository studentRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param hierarchy         used to validate the hierarchy and load the aggregates
     */
    public DetachStudentFromGroupService(StudentRepository studentRepository, HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(DetachStudentFromGroupCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        GroupId groupId = new GroupId(command.groupId());
        StudentId studentId = new StudentId(command.studentId());

        hierarchy.requireGroup(centerId, courseId, groupId);
        Student student = hierarchy.requireStudent(centerId, studentId);

        student.detachFromGroup(groupId);
        studentRepository.save(student);
    }
}