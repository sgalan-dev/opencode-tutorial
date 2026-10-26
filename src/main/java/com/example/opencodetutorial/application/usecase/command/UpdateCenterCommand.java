package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Center}.
 *
 * @param id   the identifier of the center to update
 * @param name the new center name
 */
public record UpdateCenterCommand(Long id, String name) {
}