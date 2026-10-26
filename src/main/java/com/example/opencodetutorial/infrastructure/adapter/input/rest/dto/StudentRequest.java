package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

/**
 * Request payload for {@code POST /api/v1/centers/{centerId}/students} and
 * {@code PUT /api/v1/centers/{centerId}/students/{studentId}}.
 *
 * @param centerId optional destination center. On create it is ignored (the center comes
 *                 from the URL); on update it transfers the student to another center,
 *                 and every supplied {@code groupId} must then belong to that center.
 * @param personId the identifier of the person the student is associated with
 * @param groupIds the optional set of group identifiers the student is enrolled in
 */
@Schema(description = "Payload to create or fully replace a student.")
public record StudentRequest(

        @Schema(
                description = "Optional destination center on update. Omit or send null to keep the center from the URL.",
                example = "1",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true)
        @Positive Long centerId,

        @Schema(
                description = "Identifier of the person the student is associated with. Must reference an existing person.",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Long personId,

        @Schema(
                description = "Set of group identifiers the student is enrolled in. Empty (or omitted) means the student has no groups. Every group must belong to the center the student ends up in.",
                example = "[1, 2]")
        Set<@NotNull @Positive Long> groupIds) {
}