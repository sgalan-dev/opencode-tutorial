package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.AssignTutorCommand;
import com.example.opencodetutorial.application.usecase.command.CreateProfessorCommand;
import com.example.opencodetutorial.application.usecase.command.UnassignTutorCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateProfessorCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.ProfessorRequest;

/**
 * Translates {@link ProfessorRequest} payloads and the professor identifiers taken
 * from the canonical URL into framework-free application commands. The center the
 * professor is reachable through always comes from the URL.
 */
public final class ProfessorRequestMapper {

    private ProfessorRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateProfessorCommand} from the parent center and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param request  the request payload, must not be null
     * @return the create command
     */
    public static CreateProfessorCommand toCreateCommand(Long centerId, ProfessorRequest request) {
        return new CreateProfessorCommand(centerId, request.personId());
    }

    /**
     * Builds an {@link UpdateProfessorCommand} from the URL identifiers and a request payload.
     *
     * @param centerId    the center the professor is currently reachable through
     * @param professorId the target professor taken from the URL
     * @param request     the request payload, must not be null
     * @return the update command
     */
    public static UpdateProfessorCommand toUpdateCommand(Long centerId, Long professorId,
                                                        ProfessorRequest request) {
        return new UpdateProfessorCommand(centerId, professorId, request.personId());
    }

    /**
     * Builds an {@link AssignTutorCommand} from the URL chain.
     *
     * @param centerId    the center taken from the URL
     * @param courseId    the course taken from the URL
     * @param groupId     the group taken from the URL
     * @param professorId the professor taken from the URL
     * @return the assign command
     */
    public static AssignTutorCommand toAssignTutorCommand(Long centerId, Long courseId, Long groupId,
                                                          Long professorId) {
        return new AssignTutorCommand(centerId, courseId, groupId, professorId);
    }

    /**
     * Builds an {@link UnassignTutorCommand} from the URL chain.
     *
     * @param centerId    the center taken from the URL
     * @param courseId    the course taken from the URL
     * @param groupId     the group taken from the URL
     * @param professorId the professor taken from the URL
     * @return the unassign command
     */
    public static UnassignTutorCommand toUnassignTutorCommand(Long centerId, Long courseId, Long groupId,
                                                              Long professorId) {
        return new UnassignTutorCommand(centerId, courseId, groupId, professorId);
    }
}
