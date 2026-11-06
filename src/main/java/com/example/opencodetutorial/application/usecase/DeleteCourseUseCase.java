package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;

/**
 * Inbound port for the "delete course" use case.
 */
public interface DeleteCourseUseCase {

    /**
     * Removes the course with the given identifiers, refusing while it still owns groups.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with that identifier exists under that center
     * @throws com.example.opencodetutorial.domain.exception.ResourceHasDependentsException
     *         if the course still owns groups
     */
    void execute(CenterId centerId, CourseId courseId);
}