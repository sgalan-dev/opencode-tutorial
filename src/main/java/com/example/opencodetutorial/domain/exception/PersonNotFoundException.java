package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.PersonId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Person} cannot be
 * found by the supplied identifier. The REST layer maps this exception to HTTP 404.
 */
public final class PersonNotFoundException extends RuntimeException {

    private final PersonId id;

    /**
     * Builds a "person not found" exception for the given identifier.
     *
     * @param id the missing person's identifier, must not be null
     */
    public PersonNotFoundException(PersonId id) {
        super("Person with id " + id.value() + " was not found");
        this.id = id;
    }

    public PersonId id() {
        return id;
    }
}
