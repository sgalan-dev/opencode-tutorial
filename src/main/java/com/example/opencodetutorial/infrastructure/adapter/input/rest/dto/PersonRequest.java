package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for {@code POST /api/v1/persons} and {@code PUT /api/v1/persons/{id}}.
 * Carries {@code jakarta.validation} annotations on purpose: validation runs here,
 * in the adapter boundary, and the resulting values are mapped to a framework-free
 * application command.
 *
 * @param name     the person's given name
 * @param surname  the person's primary surname
 * @param surname2 the person's optional second surname; may be omitted or {@code null}
 */
@Schema(description = "Payload to create or fully replace a person.")
public record PersonRequest(

        @Schema(
                description = "Given name of the person.",
                example = "Ada",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100)
        @NotBlank @Size(max = 100) String name,

        @Schema(
                description = "Primary family name (surname) of the person.",
                example = "Lovelace",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100)
        @NotBlank @Size(max = 100) String surname,

        @Schema(
                description = "Optional second family name (surname) of the person. Omit or send null when the person has no second surname.",
                example = "Byron",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                maxLength = 100,
                nullable = true)
        @Size(max = 100) String surname2) {
}
