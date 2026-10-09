package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.DetachStudentFromGroupCommand;

/**
 * Inbound port for the "remove student from group" use case.
 */
public interface DetachStudentFromGroupUseCase {

    /**
     * Removes the given student from the given group.
     *
     * @param command the removal payload, must not be null
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier
     */
    void execute(DetachStudentFromGroupCommand command);
}
