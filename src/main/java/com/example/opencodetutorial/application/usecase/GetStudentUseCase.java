package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the "get student by id" use case.
 */
public interface GetStudentUseCase {

    /**
     * Looks up a student by the center it belongs to and its own identifier.
     *
     * @param centerId  the center identifier, must not be null
     * @param studentId the student identifier, must not be null
     * @return the requested student
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student with that identifier exists under that center
     */
    Student execute(CenterId centerId, StudentId studentId);
}