package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new
 * {@link com.example.opencodetutorial.domain.model.Subject} inside a course.
 *
 * @param centerId the identifier of the center that owns the course
 * @param courseId the identifier of the owning course
 * @param name     the desired subject name
 */
public record CreateSubjectCommand(Long centerId, Long courseId, String name) {
}
