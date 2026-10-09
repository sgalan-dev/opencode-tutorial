package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to enrol an existing student in an existing group.
 *
 * @param studentId the identifier of the student
 * @param groupId   the identifier of the group
 */
public record AssignStudentToGroupCommand(Long studentId, Long groupId) {
}
