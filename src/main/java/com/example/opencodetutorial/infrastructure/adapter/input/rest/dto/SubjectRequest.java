package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /api/v1/centers/{centerId}/courses/{courseId}/subjects}
 * and {@code PUT /api/v1/centers/{centerId}/courses/{courseId}/subjects/{subjectId}}.
 *
 * @param name the subject's display name
 */
@Schema(description = "Payload to create or fully replace a subject.")
public record SubjectRequest(

        @Schema(
                description = "Display name of the subject.",
                example = "Matemáticas",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 150)
        @NotBlank @Size(max = 150) String name) {
}
