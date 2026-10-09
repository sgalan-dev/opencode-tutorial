package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Display name of a {@link Group}. Enforces a non-blank, length-capped value
 * through the compact constructor so the invariant cannot be bypassed by
 * direct construction.
 *
 * @param value the group name text, between 1 and 150 characters once trimmed
 */
public record GroupName(String value) {

    private static final int MAX_LENGTH = 150;

    public GroupName {
        Objects.requireNonNull(value, "GroupName must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("GroupName must not be blank");
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "GroupName must be at most " + MAX_LENGTH + " characters");
        }
        // Normalize so equality and persistence are stable regardless of surrounding whitespace.
        value = trimmed;
    }
}
