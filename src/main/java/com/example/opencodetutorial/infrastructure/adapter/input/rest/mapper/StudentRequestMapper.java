package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.CreateStudentCommand;
import com.example.opencodetutorial.application.usecase.command.DetachStudentFromGroupCommand;
import com.example.opencodetutorial.application.usecase.command.EnrollStudentInGroupCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateStudentCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentRequest;

import java.util.Set;

/**
 * Translates {@link StudentRequest} payloads into framework-free application commands.
 * The center the student is reachable through always comes from the canonical URL; the
 * optional {@code centerId} of the payload is the destination of a transfer.
 */
public final class StudentRequestMapper {

    private StudentRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateStudentCommand} from the parent center and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param request  the request payload, must not be null
     * @return the create command
     */
    public static CreateStudentCommand toCreateCommand(Long centerId, StudentRequest request) {
        return new CreateStudentCommand(centerId, request.personId(), copy(request.groupIds()));
    }

    /**
     * Builds an {@link UpdateStudentCommand} from the URL identifiers and a request payload.
     *
     * @param centerId the center the student is currently reachable through
     * @param id       the target student taken from the URL
     * @param request  the request payload, must not be null
     * @return the update command
     */
    public static UpdateStudentCommand toUpdateCommand(Long centerId, Long id, StudentRequest request) {
        return new UpdateStudentCommand(centerId, id, request.centerId(), request.personId(),
                copy(request.groupIds()));
    }

    /**
     * Builds an {@link EnrollStudentInGroupCommand} from the URL chain.
     *
     * @param centerId  the center taken from the URL
     * @param courseId  the course taken from the URL
     * @param groupId   the group taken from the URL
     * @param studentId the student taken from the URL
     * @return the enrol command
     */
    public static EnrollStudentInGroupCommand toEnrollCommand(Long centerId, Long courseId, Long groupId,
                                                              Long studentId) {
        return new EnrollStudentInGroupCommand(centerId, courseId, groupId, studentId);
    }

    /**
     * Builds a {@link DetachStudentFromGroupCommand} from the URL chain.
     *
     * @param centerId  the center taken from the URL
     * @param courseId  the course taken from the URL
     * @param groupId   the group taken from the URL
     * @param studentId the student taken from the URL
     * @return the detach command
     */
    public static DetachStudentFromGroupCommand toDetachCommand(Long centerId, Long courseId, Long groupId,
                                                                Long studentId) {
        return new DetachStudentFromGroupCommand(centerId, courseId, groupId, studentId);
    }

    private static Set<Long> copy(Set<Long> source) {
        return source == null ? Set.of() : Set.copyOf(source);
    }
}