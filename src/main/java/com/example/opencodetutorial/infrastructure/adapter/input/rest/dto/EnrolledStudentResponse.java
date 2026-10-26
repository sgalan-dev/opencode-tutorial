package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * One student enrolled in a group, together with the personal data of the person it is
 * associated with.
 *
 * @param id       the student's identifier
 * @param centerId the center the student belongs to
 * @param personId the associated person's identifier
 * @param person   the personal data of the associated person
 */
@Schema(description = "Student enrolled in a group, with the personal data of its person.")
public record EnrolledStudentResponse(

        @Schema(description = "Identifier of the enrolled student.", example = "1")
        Long id,

        @Schema(description = "Identifier of the center the student belongs to.", example = "1")
        Long centerId,

        @Schema(description = "Identifier of the person the student is associated with.", example = "1")
        Long personId,

        @Schema(description = "Personal data of the associated person.")
        PersonSummaryResponse person) {

    /**
     * Adapts the application-layer view into the REST payload.
     *
     * @param view the assembled view, must not be null
     * @return the response payload
     */
    public static EnrolledStudentResponse from(GroupDetailView.EnrolledStudentView view) {
        return new EnrolledStudentResponse(
                view.student().id().value(),
                view.student().centerId().value(),
                view.student().personId().value(),
                PersonSummaryResponse.from(view.person()));
    }
}