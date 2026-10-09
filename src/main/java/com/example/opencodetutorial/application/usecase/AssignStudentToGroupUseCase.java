package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.AssignStudentToGroupCommand;

/**
 * Inbound port for the "enrol student in group" use case.
 */
public interface AssignStudentToGroupUseCase {

    /**
     * Enrols the given student in the given group.
     *
     * @param command the enrolment payload, must not be null
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier
     */
    void execute(AssignStudentToGroupCommand command);
}
