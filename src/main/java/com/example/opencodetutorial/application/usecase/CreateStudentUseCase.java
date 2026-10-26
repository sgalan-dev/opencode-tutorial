package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateStudentCommand;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the "create student" use case.
 */
public interface CreateStudentUseCase {

    /**
     * Creates a new student inside the given center and returns the identifier assigned
     * by the persistence layer.
     *
     * @param command the data required to create the student, must not be null
     * @return the identifier of the newly created student
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.PersonNotFoundException
     *         if no person exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if one of the supplied groups does not exist or belongs to another center
     * @throws com.example.opencodetutorial.domain.exception.StudentAlreadyEnrolledInCourseException
     *         if two supplied groups share the same course
     */
    StudentId execute(CreateStudentCommand command);
}