package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

/**
 * Request payload for {@code POST /api/v1/students} and {@code PUT /api/v1/students/{id}}.
 *
 * @param personId the identifier of the person the student is associated with
 * @param groupIds the optional set of group identifiers the student is enrolled in
 */
@Schema(description = "Payload to create or fully replace a student.")
public record StudentRequest(

        @Schema(
                description = "Identifier of the person the student is associated with. Must reference an existing person.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Long personId,

        @Schema(
                description = "Set of group identifiers the student is enrolled in. Empty (or omitted) means the student has no groups.",
                example = "[1, 2]")
        Set<@NotNull @Positive Long> groupIds) {
}
