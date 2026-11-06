package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new
 * {@link com.example.opencodetutorial.domain.model.Group} inside a course.
 *
 * @param centerId the identifier of the center that owns the course
 * @param courseId the identifier of the owning course
 * @param name     the desired group name
 */
public record CreateGroupCommand(Long centerId, Long courseId, String name) {
}