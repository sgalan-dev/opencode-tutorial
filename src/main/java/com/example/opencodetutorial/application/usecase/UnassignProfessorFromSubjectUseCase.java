package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UnassignProfessorCommand;

/**
 * Inbound port for the "remove professor from subject" use case.
 */
public interface UnassignProfessorFromSubjectUseCase {

    /**
     * Removes the professor that teaches the given subject. Idempotent: removing a
     * professor that is not the current one is a no-op.
     *
     * @param command the removal payload with the full hierarchy, must not be null
     * @throws com.example.opencodetutorial.domain.exception.SubjectNotFoundException
     *         if no subject exists with the supplied identifier under that course
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     */
    void execute(UnassignProfessorCommand command);
}
