package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to remove the professor that teaches a subject, with
 * the full {@code center -> course -> subject} chain taken from the canonical URL.
 *
 * @param centerId    the identifier of the center that owns the course
 * @param courseId    the identifier of the course that owns the subject
 * @param subjectId   the identifier of the subject to remove the professor from
 * @param professorId the identifier of the professor that is expected to be the teacher
 */
public record UnassignProfessorCommand(Long centerId, Long courseId, Long subjectId, Long professorId) {
}
