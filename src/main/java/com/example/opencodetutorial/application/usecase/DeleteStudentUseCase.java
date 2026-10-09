package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the "delete student" use case.
 */
public interface DeleteStudentUseCase {

    /**
     * Removes the student with the given identifier.
     *
     * @param id the student identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier
     */
    void execute(StudentId id);
}
