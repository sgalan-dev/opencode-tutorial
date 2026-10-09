package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to remove an existing student from an existing group.
 *
 * @param studentId the identifier of the student
 * @param groupId   the identifier of the group
 */
public record DetachStudentFromGroupCommand(Long studentId, Long groupId) {
}
