package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateStudentCommand;
import com.example.opencodetutorial.domain.model.Student;

/**
 * Inbound port for the "update student" use case.
 */
public interface UpdateStudentUseCase {

    /**
     * Replaces the given student with the values from the command (person
     * association and group memberships).
     *
     * @param command the update payload, must not be null
     * @return the updated student (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier
     */
    Student execute(UpdateStudentCommand command);
}
