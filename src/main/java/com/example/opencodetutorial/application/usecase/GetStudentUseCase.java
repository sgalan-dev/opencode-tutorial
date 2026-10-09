package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the "get student by id" use case.
 */
public interface GetStudentUseCase {

    /**
     * Looks up a student by its identifier.
     *
     * @param id the student identifier, must not be null
     * @return the student with the given identifier
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the given identifier
     */
    Student execute(StudentId id);
}
