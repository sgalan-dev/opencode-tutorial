package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Group;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for group-related endpoints.
 *
 * @param id       the group's identifier
 * @param courseId the identifier of the course that owns the group
 * @param name     the group's display name
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
        String name) {

    /**
     * Adapts a domain {@link Group} aggregate into a {@link GroupResponse}.
     *
     * @param group the source aggregate, must not be null
     * @return the response payload
     */
    public static GroupResponse from(Group group) {
        return new GroupResponse(group.id().value(), group.courseId().value(), group.name().value());
    }
}