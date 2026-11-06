package com.example.opencodetutorial.application.usecase.command;

/**
 * Command carrying the data needed to create a new
 * {@link com.example.opencodetutorial.domain.model.Center}.
 *
 * @param name the desired center name
 */
public record CreateCenterCommand(String name) {
}