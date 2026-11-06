package com.example.opencodetutorial.domain.model;

import java.util.Objects;

/**
 * Strongly-typed identifier for a {@link Course}.
 * Wraps a {@link Long} so the type system rejects accidental mixing with
 * other identifiers and so the non-null / non-negative invariant lives next
 * to the value it protects.
 *
 * @param value the underlying identifier, must not be null and must be positive
 */
public record CourseId(Long value) {

    public CourseId {
        Objects.requireNonNull(value, "CourseId value must not be null");
        if (value <= 0) {
            throw new IllegalArgumentException("CourseId value must be positive");
        }
    }
}