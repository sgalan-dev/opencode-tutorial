package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new {@link com.example.opencodetutorial.domain.model.Person}.
 * Intentionally a plain carrier with raw types: validation lives in the corresponding
 * domain value objects ({@link com.example.opencodetutorial.domain.model.PersonName},
 * {@link com.example.opencodetutorial.domain.model.PersonSurname},
 * {@link com.example.opencodetutorial.domain.model.SecondSurname}) once the command
 * is mapped into the domain by the use case implementation.
 *
 * @param name     the desired given name
 * @param surname  the desired primary surname
 * @param surname2 the desired second surname, or {@code null} when the person has none
 */
public record CreatePersonCommand(String name, String surname, String surname2) {
}
