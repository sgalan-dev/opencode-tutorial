package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Second family name (surname) of a {@link Person}. Unlike {@link PersonSurname},
 * this value object is OPTIONAL: the compact constructor accepts {@code null} and
 * treats the absence of a second surname as a legitimate state. When a value is
 * supplied it is trimmed, required to be non-blank, and capped at the same length
 * as the primary surname so the invariant cannot be bypassed by direct construction.
 *
 * @param value the second surname text (between 1 and 100 characters once trimmed),
 *              or {@code null} when the person has no second surname
 */
public record SecondSurname(String value) {

    private static final int MAX_LENGTH = 100;

    public SecondSurname {
        if (value == null) {
            // Null is an accepted representation of "no second surname".
            value = null;
        } else {
            String trimmed = value.trim();
            if (trimmed.isEmpty()) {
                throw new IllegalArgumentException("SecondSurname must not be blank when provided");
            }
            if (trimmed.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "SecondSurname must be at most " + MAX_LENGTH + " characters");
            }
            // Normalize so equality and persistence are stable regardless of surrounding whitespace.
            value = trimmed;
        }
    }

    /**
     * Tells whether this value object represents the absence of a second surname.
     *
     * @return {@code true} when the wrapped value is {@code null}
     */
    public boolean isEmpty() {
        return value == null;
    }
}
