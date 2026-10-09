package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new {@link com.example.opencodetutorial.domain.model.Group}.
 *
 * @param name the desired group name
 */
public record CreateGroupCommand(String name) {
}
