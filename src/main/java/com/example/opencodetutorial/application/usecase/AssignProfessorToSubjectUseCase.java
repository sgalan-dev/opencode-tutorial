package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.AssignProfessorCommand;

/**
 * Inbound port for the "assign professor to subject" use case.
 */
public interface AssignProfessorToSubjectUseCase {

    /**
     * Assigns the given professor as the teacher of the given subject, replacing the
     * professor it had, if any. Idempotent: assigning the professor the subject already
     * has is a no-op.
     *
     * @param command the assignment payload with the full hierarchy, must not be null
     * @throws com.example.opencodetutorial.domain.exception.SubjectNotFoundException
     *         if no subject exists with the supplied identifier under that course
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.SubjectProfessorInDifferentCenterException
     *         if the professor does not belong to the center the subject belongs to
     */
    void execute(AssignProfessorCommand command);
}
