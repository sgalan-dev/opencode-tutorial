package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Group}.
 *
 * @param centerId the identifier of the center that owns the course
 * @param courseId the identifier of the course that owns the group
 * @param id       the identifier of the group to update
 * @param name     the new group name
 */
public record UpdateGroupCommand(Long centerId, Long courseId, Long id, String name) {
}