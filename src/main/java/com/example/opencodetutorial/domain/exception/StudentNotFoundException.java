package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Student} cannot be
 * found by the supplied identifier. The REST layer maps this exception to HTTP 404.
 */
public final class StudentNotFoundException extends RuntimeException {

    private final StudentId id;

    /**
     * Builds a "student not found" exception for the given identifier.
     *
     * @param id the missing student's identifier, must not be null
     */
    public StudentNotFoundException(StudentId id) {
        super("Student with id " + id.value() + " was not found");
        this.id = id;
    }

    public StudentId id() {
        return id;
    }
}
