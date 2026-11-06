package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to enrol an existing student in an existing group,
 * with the full {@code center -> course -> group} chain taken from the canonical URL.
 *
 * @param centerId  the identifier of the center that owns the course
 * @param courseId  the identifier of the course that owns the group
 * @param groupId   the identifier of the group to enrol the student in
 * @param studentId the identifier of the student to enrol
 */
public record EnrollStudentInGroupCommand(Long centerId, Long courseId, Long groupId, Long studentId) {
}