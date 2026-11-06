package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.CreateCourseCommand;
import com.example.opencodetutorial.domain.model.CourseId;

/**
 * Inbound port for the "create course" use case.
 */
public interface CreateCourseUseCase {

    /**
     * Creates a new course inside the given center and returns the identifier assigned
     * by the persistence layer.
     *
     * @param command the data required to create the course, must not be null
     * @return the identifier of the newly created course
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.DuplicateNameException
     *         if the center already has a course with the supplied name
     */
    CourseId execute(CreateCourseCommand command);
}