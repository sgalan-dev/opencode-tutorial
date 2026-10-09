package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to fully replace an existing
 * {@link com.example.opencodetutorial.domain.model.Group}.
 *
 * @param id   the identifier of the group to update
 * @param name the new group name
 */
public record UpdateGroupCommand(Long id, String name) {
}
