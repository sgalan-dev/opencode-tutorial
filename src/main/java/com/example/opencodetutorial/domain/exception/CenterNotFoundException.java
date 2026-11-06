package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CenterId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Center} cannot be
 * found by the supplied identifier. The REST layer maps this exception to HTTP 404.
 */
public final class CenterNotFoundException extends RuntimeException {

    private final CenterId id;

    /**
     * Builds a "center not found" exception for the given identifier.
     *
     * @param id the missing center's identifier, must not be null
     */
    public CenterNotFoundException(CenterId id) {
        super("Center with id " + id.value() + " was not found");
        this.id = id;
    }

    public CenterId id() {
        return id;
    }
}