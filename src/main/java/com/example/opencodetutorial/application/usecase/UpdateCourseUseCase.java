package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateCourseCommand;
import com.example.opencodetutorial.domain.model.Course;

/**
 * Inbound port for the "update course" use case.
 */
public interface UpdateCourseUseCase {

    /**
     * Replaces the given course with the values from the command, as long as the course
     * belongs to the supplied center.
     *
     * @param command the update payload, must not be null
     * @return the updated course (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with the supplied identifier exists under that center
     * @throws com.example.opencodetutorial.domain.exception.DuplicateNameException
     *         if the center already has a course with the supplied name
     */
    Course execute(UpdateCourseCommand command);
}