package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.AssignTutorCommand;

/**
 * Inbound port for the "assign tutor to group" use case.
 */
public interface AssignTutorToGroupUseCase {

    /**
     * Assigns the given professor as the tutor of the given group, replacing the tutor
     * the group had, if any. Idempotent: assigning the tutor the group already has is
     * a no-op.
     *
     * @param command the assignment payload with the full hierarchy, must not be null
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier under that course
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.TutorInDifferentCenterException
     *         if the professor does not belong to the center the group belongs to
     */
    void execute(AssignTutorCommand command);
}
