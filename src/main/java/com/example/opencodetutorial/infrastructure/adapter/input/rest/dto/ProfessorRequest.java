package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request payload for {@code POST /api/v1/centers/{centerId}/professors} and
 * {@code PUT /api/v1/centers/{centerId}/professors/{professorId}}.
 *
 * @param personId the identifier of the person the professor is associated with
 */
@Schema(description = "Payload to create or fully replace a professor.")
public record ProfessorRequest(

        @Schema(
                description = "Identifier of the person the professor is associated with. Must reference an existing person.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Long personId) {
}
