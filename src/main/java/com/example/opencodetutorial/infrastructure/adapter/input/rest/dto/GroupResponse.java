package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.ProfessorId;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for group-related endpoints.
 *
 * @param id              the group's identifier
 * @param courseId        the identifier of the course that owns the group
 * @param name            the group's display name
 * @param tutorProfessorId the identifier of the professor that tutors the group, or
 *                         {@code null} when the group has no tutor
 */
@Schema(description = "Group resource as returned by the API. Persisted to the `student_group` table.")
public record GroupResponse(

        @Schema(description = "Auto-generated identifier of the group.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Identifier of the course that owns the group.", example = "1")
        Long courseId,

        @Schema(description = "Display name of the group.", example = "Grupo A")
        String name,

        @Schema(description = "Identifier of the professor that tutors the group. Null when the group has no tutor.",
                example = "1",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true)
        Long tutorProfessorId) {

    /**
     * Adapts a domain {@link Group} aggregate into a {@link GroupResponse}.
     *
     * @param group the source aggregate, must not be null
     * @return the response payload
     */
    public static GroupResponse from(Group group) {
        ProfessorId tutor = group.tutorProfessorId();
        return new GroupResponse(
                group.id().value(),
                group.courseId().value(),
                group.name().value(),
                tutor == null ? null : tutor.value());
    }
}
