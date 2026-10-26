package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.Course;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for course-related endpoints.
 *
 * @param id       the course's identifier
 * @param centerId the identifier of the center that owns the course
 * @param name     the course's display name
 */
@Schema(description = "Course resource as returned by the API.")
public record CourseResponse(

        @Schema(description = "Auto-generated identifier of the course.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Identifier of the center that owns the course.", example = "1")
        Long centerId,

        @Schema(description = "Display name of the course.", example = "Primero Primaria")
        String name) {

    /**
     * Adapts a domain {@link Course} aggregate into a {@link CourseResponse}.
     *
     * @param course the source aggregate, must not be null
     * @return the response payload
     */
    public static CourseResponse from(Course course) {
        return new CourseResponse(course.id().value(), course.centerId().value(), course.name().value());
    }
}