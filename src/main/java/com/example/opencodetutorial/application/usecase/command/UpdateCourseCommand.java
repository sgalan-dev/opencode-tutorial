package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Course}.
 *
 * @param centerId the identifier of the center the course must belong to
 * @param id       the identifier of the course to update
 * @param name     the new course name
 */
public record UpdateCourseCommand(Long centerId, Long id, String name) {
}