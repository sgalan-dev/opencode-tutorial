package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to assign the professor that teaches a subject, with
 * the full {@code center -> course -> subject} chain taken from the canonical URL.
 *
 * @param centerId    the identifier of the center that owns the course
 * @param courseId    the identifier of the course that owns the subject
 * @param subjectId   the identifier of the subject to assign the professor to
 * @param professorId the identifier of the professor that becomes the teacher
 */
public record AssignProfessorCommand(Long centerId, Long courseId, Long subjectId, Long professorId) {
}
