package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DetachStudentFromGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.DetachStudentFromGroupCommand;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link DetachStudentFromGroupUseCase} implementation. The aggregate's
 * {@code detachFromGroup} is idempotent: if the student was not enrolled, the
 * operation is a no-op and no exception is raised.
 */
@Service
public final class DetachStudentFromGroupService implements DetachStudentFromGroupUseCase {

    private final StudentRepository studentRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param studentRepository the student repository, must not be null
     */
    public DetachStudentFromGroupService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void execute(DetachStudentFromGroupCommand command) {
        StudentId studentId = new StudentId(command.studentId());
        GroupId groupId = new GroupId(command.groupId());

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        student.detachFromGroup(groupId);
        studentRepository.save(student);
    }
}
