package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Person}.
 *
 * @param id       the identifier of the person to update
 * @param name     the new given name
 * @param surname  the new primary surname
 * @param surname2 the new second surname, or {@code null} to clear it
 */
public record UpdatePersonCommand(Long id, String name, String surname, String surname2) {
}
