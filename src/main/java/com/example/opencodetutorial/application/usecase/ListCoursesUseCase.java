package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;

import java.util.List;

/**
 * Inbound port for the "list courses of a center" use case.
 */
public interface ListCoursesUseCase {

    /**
     * Returns every course belonging to the given center, in no particular order.
     *
     * @param centerId the center identifier, must not be null
     * @return the courses of that center, never null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     */
    List<Course> execute(CenterId centerId);
}