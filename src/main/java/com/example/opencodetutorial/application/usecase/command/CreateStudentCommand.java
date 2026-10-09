package com.example.opencodetutorial.application.usecase.command;

import java.util.Set;

/**
 * Command carrying the data needed to create a new {@link com.example.opencodetutorial.domain.model.Student}.
 *
 * @param personId the identifier of the person the student is associated with
 * @param groupIds the optional set of group identifiers the student should be enrolled in
 */
public record CreateStudentCommand(Long personId, Set<Long> groupIds) {
}
