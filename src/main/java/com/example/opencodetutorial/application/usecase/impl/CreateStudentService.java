package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateStudentUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateStudentCommand;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default {@link CreateStudentUseCase} implementation.
 */
@Service
public final class CreateStudentService implements CreateStudentUseCase {

    private final StudentRepository studentRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param studentRepository the student repository, must not be null
     */
    public CreateStudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentId execute(CreateStudentCommand command) {
        PersonId personId = new PersonId(command.personId());
        Set<GroupId> groupIds = toGroupIds(command.groupIds());

        Student student = Student.create(personId, groupIds);
        return studentRepository.save(student).id();
    }

    private Set<GroupId> toGroupIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return ids.stream().map(GroupId::new).collect(Collectors.toUnmodifiableSet());
    }
}
