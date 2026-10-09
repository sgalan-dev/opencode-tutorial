package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Family name (surname) of a {@link Person}. Enforces a non-blank, length-capped
 * value through the compact constructor so the invariant cannot be bypassed by
 * direct construction.
 *
 * @param value the surname text, between 1 and 100 characters once trimmed
 */
public record PersonSurname(String value) {

    private static final int MAX_LENGTH = 100;

    public PersonSurname {
        Objects.requireNonNull(value, "PersonSurname must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("PersonSurname must not be blank");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "PersonSurname must be at most " + MAX_LENGTH + " characters");
        }
        // Normalize so equality and persistence are stable regardless of surrounding whitespace.
        value = trimmed;
    }
}
