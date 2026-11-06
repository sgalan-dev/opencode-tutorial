package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Center;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for center-related endpoints.
 *
 * @param id   the center's identifier
 * @param name the center's display name
 */
@Schema(description = "Center resource as returned by the API.")
public record CenterResponse(

        @Schema(description = "Auto-generated identifier of the center.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Display name of the center.", example = "Centro de prueba 1")
        String name) {

    /**
     * Adapts a domain {@link Center} aggregate into a {@link CenterResponse}.
     *
     * @param center the source aggregate, must not be null
     * @return the response payload
     */
    public static CenterResponse from(Center center) {
        return new CenterResponse(center.id().value(), center.name().value());
    }
}