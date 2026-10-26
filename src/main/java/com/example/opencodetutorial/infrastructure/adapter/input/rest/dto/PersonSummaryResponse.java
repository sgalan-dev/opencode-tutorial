package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Personal data of a person, embedded in the assembled detail responses.
 *
 * @param name     the person's given name
 * @param surname  the person's primary surname
 * @param surname2 the person's optional second surname, or {@code null} when absent
 */
@Schema(description = "Personal data of the person behind a student, a professor or any other resource.")
public record PersonSummaryResponse(

        @Schema(description = "Given name of the person.", example = "Ada")
        String name,

        @Schema(description = "Primary family name (surname) of the person.", example = "Lovelace")
        String surname,

        @Schema(description = "Optional second family name (surname). Null when absent.",
                example = "Byron",
                nullable = true)
        String surname2) {

    /**
     * Adapts a domain {@link Person} aggregate into a {@link PersonSummaryResponse}.
     *
     * @param person the source aggregate, must not be null
     * @return the response payload
     */
    public static PersonSummaryResponse from(Person person) {
        return new PersonSummaryResponse(
                person.name().value(),
                person.surname().value(),
                person.surname2() == null ? null : person.surname2().value());
    }
}