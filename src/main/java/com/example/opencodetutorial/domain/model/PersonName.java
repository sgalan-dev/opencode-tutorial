package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Given name of a {@link Person}. Enforces a non-blank, length-capped value
 * through the compact constructor so the invariant cannot be bypassed by
 * direct construction.
 *
 * @param value the name text, between 1 and 100 characters once trimmed
 */
public record PersonName(String value) {

    private static final int MAX_LENGTH = 100;

    public PersonName {
        Objects.requireNonNull(value, "PersonName must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("PersonName must not be blank");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "PersonName must be at most " + MAX_LENGTH + " characters");
        }
        // Normalize so equality and persistence are stable regardless of surrounding whitespace.
        value = trimmed;
    }
}
