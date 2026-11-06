package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Professor} cannot be
 * found by the supplied identifier, or when it belongs to another center than the one
 * it is requested under. The REST layer maps this exception to HTTP 404.
 */
public final class ProfessorNotFoundException extends RuntimeException {

    private final ProfessorId id;

    /**
     * Builds a "professor not found" exception for the given identifier.
     *
     * @param id the missing professor's identifier, must not be null
     */
    public ProfessorNotFoundException(ProfessorId id) {
        super("Professor with id " + id.value() + " was not found");
        this.id = id;
    }

    public ProfessorId id() {
        return id;
    }
}
