package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.EnrollStudentInGroupCommand;

/**
 * Inbound port for the "enrol student in group" use case.
 */
public interface EnrollStudentInGroupUseCase {

    /**
     * Enrols the given student in the given group. Idempotent: enrolling a student that
     * is already in the group is a no-op.
     *
     * @param command the enrolment payload with the full hierarchy, must not be null
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier under that course
     * @throws com.example.opencodetutorial.domain.exception.StudentInDifferentCenterException
     *         if the student does not belong to the center in the URL
     * @throws com.example.opencodetutorial.domain.exception.StudentAlreadyEnrolledInCourseException
     *         if the student is already enrolled in another group of the same course
     */
    void execute(EnrollStudentInGroupCommand command);
}