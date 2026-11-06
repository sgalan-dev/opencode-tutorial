package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Display name of a {@link Subject}. Enforces a non-blank, length-capped value
 * through the compact constructor so the invariant cannot be bypassed by
 * direct construction.
 *
 * @param value the subject name text, between 1 and 150 characters once trimmed
 */
public record SubjectName(String value) {

    private static final int MAX_LENGTH = 150;

    public SubjectName {
        Objects.requireNonNull(value, "SubjectName must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("SubjectName must not be blank");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "SubjectName must be at most " + MAX_LENGTH + " characters");
        }
        // Normalize so equality and persistence are stable regardless of surrounding whitespace.
        value = trimmed;
    }
}
