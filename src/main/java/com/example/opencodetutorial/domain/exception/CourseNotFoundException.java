package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CourseId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Course} cannot be
 * found by the supplied identifier. The REST layer maps this exception to HTTP 404.
 */
public final class CourseNotFoundException extends RuntimeException {

    private final CourseId id;

    /**
     * Builds a "course not found" exception for the given identifier.
     *
     * @param id the missing course's identifier, must not be null
     */
    public CourseNotFoundException(CourseId id) {
        super("Course with id " + id.value() + " was not found");
        this.id = id;
    }

    public CourseId id() {
        return id;
    }
}