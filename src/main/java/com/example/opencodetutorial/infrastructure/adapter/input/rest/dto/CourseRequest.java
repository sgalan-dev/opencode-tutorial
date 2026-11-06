package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /api/v1/centers/{centerId}/courses} and
 * {@code PUT /api/v1/centers/{centerId}/courses/{courseId}}. The owning center comes
 * from the URL, so it is not part of the payload.
 *
 * @param name the course's display name
 */
@Schema(description = "Payload to create or fully replace a course.")
public record CourseRequest(

        @Schema(
                description = "Display name of the course. Must be unique within the center.",
                example = "Primero Primaria",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 150)
        @NotBlank @Size(max = 150) String name) {
}