package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.AssignStudentToGroupCommand;
import com.example.opencodetutorial.application.usecase.command.CreateStudentCommand;
import com.example.opencodetutorial.application.usecase.command.DetachStudentFromGroupCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateStudentCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentRequest;

import java.util.Set;

/**
 * Translates {@link StudentRequest} payloads into framework-free application commands.
 */
public final class StudentRequestMapper {

    private StudentRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateStudentCommand} from a request payload.
     *
     * @param request the request payload, must not be null
     * @return the create command
     */
    public static CreateStudentCommand toCreateCommand(StudentRequest request) {
        return new CreateStudentCommand(request.personId(), copy(request.groupIds()));
    }

    /**
     * Builds an {@link UpdateStudentCommand} from an identifier and a request payload.
     *
     * @param id      the target student identifier
     * @param request the request payload, must not be null
     * @return the update command
     */
    public static UpdateStudentCommand toUpdateCommand(Long id, StudentRequest request) {
        return new UpdateStudentCommand(id, request.personId(), copy(request.groupIds()));
    }

    /**
     * Builds an {@link AssignStudentToGroupCommand} from path variables.
     *
     * @param studentId the student identifier
     * @param groupId   the group identifier
     * @return the assign command
     */
    public static AssignStudentToGroupCommand toAssignCommand(Long studentId, Long groupId) {
        return new AssignStudentToGroupCommand(studentId, groupId);
    }

    /**
     * Builds a {@link DetachStudentFromGroupCommand} from path variables.
     *
     * @param studentId the student identifier
     * @param groupId   the group identifier
     * @return the detach command
     */
    public static DetachStudentFromGroupCommand toDetachCommand(Long studentId, Long groupId) {
        return new DetachStudentFromGroupCommand(studentId, groupId);
    }

    private static Set<Long> copy(Set<Long> source) {
        return source == null ? Set.of() : Set.copyOf(source);
    }
}
