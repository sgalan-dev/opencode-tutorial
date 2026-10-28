package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.SubjectId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Subject} cannot be
 * found by the supplied identifier, or when it belongs to another course than the one
 * it is requested under. The REST layer maps this exception to HTTP 404.
 */
public final class SubjectNotFoundException extends RuntimeException {

    private final SubjectId id;

    /**
     * Builds a "subject not found" exception for the given identifier.
     *
     * @param id the missing subject's identifier, must not be null
     */
    public SubjectNotFoundException(SubjectId id) {
        super("Subject with id " + id.value() + " was not found");
        this.id = id;
    }

    public SubjectId id() {
        return id;
    }
}
