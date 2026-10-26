package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new
 * {@link com.example.opencodetutorial.domain.model.Course} inside a center.
 *
 * @param centerId the identifier of the owning center
 * @param name     the desired course name
 */
public record CreateCourseCommand(Long centerId, String name) {
}