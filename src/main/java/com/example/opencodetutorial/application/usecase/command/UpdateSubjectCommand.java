package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Subject}. The professor that
 * teaches the subject is not part of the command: it has its own operation.
 *
 * @param centerId the identifier of the center that owns the course
 * @param courseId the identifier of the course that owns the subject
 * @param id       the identifier of the subject to update
 * @param name     the new subject name
 */
public record UpdateSubjectCommand(Long centerId, Long courseId, Long id, String name) {
}
