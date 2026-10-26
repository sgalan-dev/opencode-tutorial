package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Student;

import java.util.List;

/**
 * Inbound port for the "list students of a center" use case.
 */
public interface ListStudentsUseCase {

    /**
     * Returns every student belonging to the given center, in no particular order.
     *
     * @param centerId the center identifier, must not be null
     * @return the students of that center, never null
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     */
    List<Student> execute(CenterId centerId);
}