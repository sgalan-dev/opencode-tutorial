package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Professor}. The center of the
 * professor never changes: it comes from the canonical URL.
 *
 * @param centerId    the center the professor is currently reachable through
 * @param id          the identifier of the professor to update
 * @param personId    the new person association
 */
public record UpdateProfessorCommand(Long centerId, Long id, Long personId) {
}
