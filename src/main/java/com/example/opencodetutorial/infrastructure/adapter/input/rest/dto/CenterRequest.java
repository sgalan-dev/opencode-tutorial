package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /api/v1/centers} and {@code PUT /api/v1/centers/{id}}.
 *
 * @param name the center's display name
 */
@Schema(description = "Payload to create or fully replace a center.")
public record CenterRequest(

        @Schema(
                description = "Display name of the center. Must be unique across all centers.",
                example = "Centro de prueba 1",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 150)
        @NotBlank @Size(max = 150) String name) {
}