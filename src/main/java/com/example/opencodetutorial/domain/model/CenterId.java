package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Strongly-typed identifier for a {@link Center}.
 * Wraps a {@link Long} so the type system rejects accidental mixing with
 * other identifiers and so the non-null / non-negative invariant lives next
 * to the value it protects.
 *
 * @param value the underlying identifier, must not be null and must be positive
 */
public record CenterId(Long value) {

    public CenterId {
        Objects.requireNonNull(value, "CenterId value must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("CenterId value must be positive");
        }
    }
}