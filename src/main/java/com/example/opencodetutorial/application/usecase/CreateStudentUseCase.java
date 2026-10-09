package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateStudentCommand;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the "create student" use case.
 */
public interface CreateStudentUseCase {

    /**
     * Creates a new student from the supplied command and returns the identifier
     * assigned by the persistence layer.
     *
     * @param command the data required to create the student, must not be null
     * @return the identifier of the newly created student
     */
    StudentId execute(CreateStudentCommand command);
}
