package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the "delete student" use case.
 */
public interface DeleteStudentUseCase {

    /**
     * Removes the student with the given identifiers together with every enrolment it
     * holds.
     *
     * @param centerId  the center identifier, must not be null
     * @param studentId the student identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier under that center
     */
    void execute(CenterId centerId, StudentId studentId);
}