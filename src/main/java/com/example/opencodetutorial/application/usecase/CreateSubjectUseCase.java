package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateSubjectCommand;
import com.example.opencodetutorial.domain.model.SubjectId;

/**
 * Inbound port for the "create subject" use case.
 */
public interface CreateSubjectUseCase {

    /**
     * Creates a new subject inside the given course of the given center and returns the
     * identifier assigned by the persistence layer.
     *
     * @param command the data required to create the subject, must not be null
     * @return the identifier of the newly created subject
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with the supplied identifier exists under that center
     * @throws com.example.opencodetutorial.domain.exception.DuplicateNameException
     *         if the course already has a subject with that name
     */
    SubjectId execute(CreateSubjectCommand command);
}
