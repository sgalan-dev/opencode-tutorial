package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.view.StudentDetailView;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Inbound port for the assembled "student detail" read model.
 */
public interface GetStudentDetailUseCase {

    /**
     * Assembles the student, its center, the personal data of its person and every
     * enrolment enriched with its group and course.
     *
     * @param centerId  the center identifier, must not be null
     * @param studentId the student identifier, must not be null
     * @return the assembled detail, with an empty enrolment list when there are none
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier under that center
     */
    StudentDetailView execute(CenterId centerId, StudentId studentId);
}