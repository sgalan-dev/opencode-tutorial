package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to remove the tutor of an existing group, with the
 * full {@code center -> course -> group} chain taken from the canonical URL.
 *
 * @param centerId    the identifier of the center that owns the course
 * @param courseId    the identifier of the course that owns the group
 * @param groupId     the identifier of the group to remove the tutor from
 * @param professorId the identifier of the professor that is expected to be the tutor
 */
public record UnassignTutorCommand(Long centerId, Long courseId, Long groupId, Long professorId) {
}
