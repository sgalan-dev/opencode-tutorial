package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateStudentUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateStudentCommand;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default {@link UpdateStudentUseCase} implementation. Replaces both the person
 * association and the full set of group enrolments in one transaction.
 */
@Service
public final class UpdateStudentService implements UpdateStudentUseCase {

    private final StudentRepository studentRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param studentRepository the student repository, must not be null
     */
    public UpdateStudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student execute(UpdateStudentCommand command) {
        StudentId id = new StudentId(command.id());
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        existing.changePerson(new PersonId(command.personId()));
        existing.replaceGroups(toGroupIds(command.groupIds()));
        return studentRepository.save(existing);
    }

    private Set<GroupId> toGroupIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return ids.stream().map(GroupId::new).collect(Collectors.toUnmodifiableSet());
    }
}
