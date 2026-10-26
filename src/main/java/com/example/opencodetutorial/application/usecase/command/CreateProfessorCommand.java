package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new
 * {@link com.example.opencodetutorial.domain.model.Professor} inside a center.
 *
 * @param centerId the identifier of the center the professor belongs to
 * @param personId the identifier of the person the professor is associated with
 */
public record CreateProfessorCommand(Long centerId, Long personId) {
}
