package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Strongly-typed identifier for a {@link Person}.
 * Wraps a {@link Long} so the type system rejects accidental mixing with
 * other identifiers and so the non-null / non-negative invariant lives next
 * to the value it protects.
 *
 * @param value the underlying identifier, must not be null and must be positive
 */
public record PersonId(Long value) {

    public PersonId {
        Objects.requireNonNull(value, "PersonId value must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("PersonId value must be positive");
        }
    }
}
