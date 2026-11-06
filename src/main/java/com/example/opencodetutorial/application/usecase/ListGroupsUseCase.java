package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;

import java.util.List;

/**
 * Inbound port for the "list groups of a course" use case.
 */
public interface ListGroupsUseCase {

    /**
     * Returns every group belonging to the given course of the given center.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @return the groups of that course, never null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with that identifier exists under that center
     */
    List<Group> execute(CenterId centerId, CourseId courseId);
}