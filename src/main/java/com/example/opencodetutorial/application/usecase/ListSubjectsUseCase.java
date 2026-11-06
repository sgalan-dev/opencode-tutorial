package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Subject;

import java.util.List;

/**
 * Inbound port for the "list subjects of a course" use case.
 */
public interface ListSubjectsUseCase {

    /**
     * Returns every subject belonging to the given course of the given center.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @return the subjects of that course, never null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with that identifier exists under that center
     */
    List<Subject> execute(CenterId centerId, CourseId courseId);
}
