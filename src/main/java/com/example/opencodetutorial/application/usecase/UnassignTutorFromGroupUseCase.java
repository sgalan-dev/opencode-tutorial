package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UnassignTutorCommand;

/**
 * Inbound port for the "remove tutor from group" use case.
 */
public interface UnassignTutorFromGroupUseCase {

    /**
     * Removes the tutor of the given group. Idempotent: removing a tutor that is not
     * the current one is a no-op.
     *
     * @param command the removal payload with the full hierarchy, must not be null
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier under that course
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     */
    void execute(UnassignTutorCommand command);
}
