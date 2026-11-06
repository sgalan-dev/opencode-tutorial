package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;

/**
 * Inbound port for the "get course by id" use case.
 */
public interface GetCourseUseCase {

    /**
     * Looks up a course by the center that owns it and its own identifier.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @return the requested course
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with that identifier exists under that center
     */
    Course execute(CenterId centerId, CourseId courseId);
}