package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateStudentCommand;
import com.example.opencodetutorial.domain.model.Student;

/**
 * Inbound port for the "update student" use case.
 */
public interface UpdateStudentUseCase {

    /**
     * Replaces the student with the values from the command: person association, the
     * full set of enrolments and, optionally, the center it belongs to.
     *
     * @param command the update payload, must not be null
     * @return the updated student (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if one of the supplied groups does not exist or belongs to another center
     */
    Student execute(UpdateStudentCommand command);
}