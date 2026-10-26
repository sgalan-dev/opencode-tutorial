package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to remove an existing student from an existing group,
 * with the full {@code center -> course -> group} chain taken from the canonical URL.
 *
 * @param centerId  the identifier of the center that owns the course
 * @param courseId  the identifier of the course that owns the group
 * @param groupId   the identifier of the group to remove the student from
 * @param studentId the identifier of the student to remove
 */
public record DetachStudentFromGroupCommand(Long centerId, Long courseId, Long groupId, Long studentId) {
}