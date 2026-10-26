package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.StudentDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * One enrolment of a student: the group plus the course that owns it.
 *
 * @param group  the enrolled group
 * @param course the course that owns the group
 */
@Schema(description = "Enrolment of a student: the group and the course it belongs to.")
public record EnrolmentResponse(

        @Schema(description = "The enrolled group.")
        GroupResponse group,

        @Schema(description = "The course that owns the group.")
        CourseResponse course) {

    /**
     * Adapts the application-layer view into the REST payload.
     *
     * @param view the assembled view, must not be null
     * @return the response payload
     */
    public static EnrolmentResponse from(StudentDetailView.EnrolmentView view) {
        return new EnrolmentResponse(
                GroupResponse.from(view.group()),
                CourseResponse.from(view.course()));
    }
}