package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.AssignProfessorCommand;
import com.example.opencodetutorial.application.usecase.command.CreateSubjectCommand;
import com.example.opencodetutorial.application.usecase.command.UnassignProfessorCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateSubjectCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.SubjectRequest;

/**
 * Translates {@link SubjectRequest} payloads and the identifiers taken from the
 * canonical URL into framework-free application commands. The parent center and course
 * always come from the URL.
 */
public final class SubjectRequestMapper {

    private SubjectRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateSubjectCommand} from the URL chain and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param courseId the parent course taken from the URL
     * @param request  the request payload, must not be null
     * @return the create command
     */
    public static CreateSubjectCommand toCreateCommand(Long centerId, Long courseId, SubjectRequest request) {
        return new CreateSubjectCommand(centerId, courseId, request.name());
    }

    /**
     * Builds an {@link UpdateSubjectCommand} from the URL chain and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param courseId the parent course taken from the URL
     * @param subjectId the target subject taken from the URL
     * @param request  the request payload, must not be null
     * @return the update command
     */
    public static UpdateSubjectCommand toUpdateCommand(Long centerId, Long courseId, Long subjectId,
                                                      SubjectRequest request) {
        return new UpdateSubjectCommand(centerId, courseId, subjectId, request.name());
    }

    /**
     * Builds an {@link AssignProfessorCommand} from the URL chain.
     *
     * @param centerId    the center taken from the URL
     * @param courseId    the course taken from the URL
     * @param subjectId   the subject taken from the URL
     * @param professorId the professor taken from the URL
     * @return the assign command
     */
    public static AssignProfessorCommand toAssignProfessorCommand(Long centerId, Long courseId, Long subjectId,
                                                                  Long professorId) {
        return new AssignProfessorCommand(centerId, courseId, subjectId, professorId);
    }

    /**
     * Builds an {@link UnassignProfessorCommand} from the URL chain.
     *
     * @param centerId    the center taken from the URL
     * @param courseId    the course taken from the URL
     * @param subjectId   the subject taken from the URL
     * @param professorId the professor taken from the URL
     * @return the unassign command
     */
    public static UnassignProfessorCommand toUnassignProfessorCommand(Long centerId, Long courseId, Long subjectId,
                                                                      Long professorId) {
        return new UnassignProfessorCommand(centerId, courseId, subjectId, professorId);
    }
}
