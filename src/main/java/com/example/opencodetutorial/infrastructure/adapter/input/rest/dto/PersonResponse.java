package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for person-related endpoints.
 *
 * @param id       the person's identifier
 * @param name     the person's given name
 * @param surname  the person's primary surname
 * @param surname2 the person's optional second surname, or {@code null} when absent
 */
@Schema(description = "Person resource as returned by the API.")
public record PersonResponse(

        @Schema(description = "Auto-generated identifier of the person.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Given name of the person.", example = "Ada")
        String name,

        @Schema(description = "Primary family name (surname) of the person.", example = "Lovelace")
        String surname,

        @Schema(description = "Optional second family name (surname) of the person. Null when the person has no second surname.",
                example = "Byron",
                nullable = true)
        String surname2) {

    /**
     * Adapts a domain {@link Person} aggregate into a {@link PersonResponse}.
     *
     * @param person the source aggregate, must not be null
     * @return the response payload
     */
    public static PersonResponse from(Person person) {
        return new PersonResponse(
                person.id().value(),
                person.name().value(),
                person.surname().value(),
                person.surname2() == null ? null : person.surname2().value());
    }
}
