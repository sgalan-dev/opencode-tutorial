package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Display name of a {@link Course}. Enforces a non-blank, length-capped value
 * through the compact constructor so the invariant cannot be bypassed by
 * direct construction.
 *
 * @param value the course name text, between 1 and 150 characters once trimmed
 */
public record CourseName(String value) {

    private static final int MAX_LENGTH = 150;

    public CourseName {
        Objects.requireNonNull(value, "CourseName must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("CourseName must not be blank");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "CourseName must be at most " + MAX_LENGTH + " characters");
        }
        // Normalize so equality and persistence are stable regardless of surrounding whitespace.
        value = trimmed;
    }
}