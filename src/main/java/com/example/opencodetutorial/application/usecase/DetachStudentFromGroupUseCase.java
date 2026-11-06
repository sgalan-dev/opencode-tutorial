package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.DetachStudentFromGroupCommand;

/**
 * Inbound port for the "remove student from group" use case.
 */
public interface DetachStudentFromGroupUseCase {

    /**
     * Removes the given student from the given group. Idempotent: removing a student
     * that is not in the group is a no-op.
     *
     * @param command the removal payload with the full hierarchy, must not be null
     * @throws com.example.opencodetutorial.domain.exception.StudentNotFoundException
     *         if no student exists with the supplied identifier under that center
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group exists with the supplied identifier under that course
     */
    void execute(DetachStudentFromGroupCommand command);
}