package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Professor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for professor-related endpoints.
 *
 * @param id       the professor's identifier
 * @param centerId the center the professor belongs to
 * @param personId the associated person's identifier
 */
@Schema(description = "Professor resource as returned by the API.")
public record ProfessorResponse(

        @Schema(description = "Auto-generated identifier of the professor.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Identifier of the center the professor belongs to.", example = "1")
        Long centerId,

        @Schema(description = "Identifier of the person the professor is associated with.",
                example = "1")
        Long personId) {

    /**
     * Adapts a domain {@link Professor} aggregate into a {@link ProfessorResponse}.
     *
     * @param professor the source aggregate, must not be null
     * @return the response payload
     */
    public static ProfessorResponse from(Professor professor) {
        return new ProfessorResponse(
                professor.id().value(),
                professor.centerId().value(),
                professor.personId().value());
    }
}
