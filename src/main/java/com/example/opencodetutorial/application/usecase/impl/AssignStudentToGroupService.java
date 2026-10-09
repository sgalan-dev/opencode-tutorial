package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.AssignStudentToGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.AssignStudentToGroupCommand;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link AssignStudentToGroupUseCase} implementation. Verifies both
 * ends exist (the group must, otherwise the student would be enrolled in a
 * phantom group), then delegates to the aggregate's {@code attachToGroup}
 * behaviour which keeps the membership set consistent.
 */
@Service
public final class AssignStudentToGroupService implements AssignStudentToGroupUseCase {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param groupRepository   the group repository, must not be null
     */
    public AssignStudentToGroupService(StudentRepository studentRepository,
                                      GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void execute(AssignStudentToGroupCommand command) {
        StudentId studentId = new StudentId(command.studentId());
        GroupId groupId = new GroupId(command.groupId());

        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException(groupId);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        student.attachToGroup(groupId);
        studentRepository.save(student);
    }
}
