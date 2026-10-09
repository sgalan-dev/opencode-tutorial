package com.example.opencodetutorial.application.usecase.command;

import java.util.Set;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Student}.
 *
 * @param id       the identifier of the student to update
 * @param personId the new person association
 * @param groupIds the new (complete) set of group enrolments
 */
public record UpdateStudentCommand(Long id, Long personId, Set<Long> groupIds) {
}
