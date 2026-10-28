package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.command.UpdateSubjectCommand;
import com.example.opencodetutorial.domain.model.Subject;

/**
 * Inbound port for the "update subject" use case.
 */
public interface UpdateSubjectUseCase {

    /**
     * Replaces the given subject with the values from the command, keeping it under the
     * same course and keeping the professor that teaches it.
     *
     * @param command the update payload, must not be null
     * @return the updated subject (with refreshed timestamps)
     * @throws com.example.opencodetutorial.domain.exception.SubjectNotFoundException
     *         if no subject with that identifier exists under the supplied course
     * @throws com.example.opencodetutorial.domain.exception.DuplicateNameException
     *         if the course already has another subject with that name
     */
    Subject execute(UpdateSubjectCommand command);
}
