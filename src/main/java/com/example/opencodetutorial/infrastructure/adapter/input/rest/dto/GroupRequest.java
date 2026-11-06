package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /api/v1/groups} and {@code PUT /api/v1/groups/{id}}.
 *
 * @param name the group's display name
 */
@Schema(description = "Payload to create or fully replace a group.")
public record GroupRequest(

        @Schema(
                description = "Display name of the group.",
                example = "Grupo A",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 150)
        @NotBlank @Size(max = 150) String name) {
}
