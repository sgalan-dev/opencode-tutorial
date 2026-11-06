package com.example.opencodetutorial.application.usecase.command;

import java.util.Set;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Student}.
 *
 * @param centerId    the center the student is currently reachable through
 * @param id          the identifier of the student to update
 * @param newCenterId the destination center, or {@code null} to keep the current one
 * @param personId    the new person association
 * @param groupIds    the new (complete) set of group enrolments
 */
public record UpdateStudentCommand(Long centerId,
                                   Long id,
                                   Long newCenterId,
                                   Long personId,
                                   Set<Long> groupIds) {
}