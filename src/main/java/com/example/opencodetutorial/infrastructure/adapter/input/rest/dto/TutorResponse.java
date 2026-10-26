package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The tutor of a group, together with the personal data of the person the tutor is
 * associated with.
 *
 * @param professorId the tutor's identifier
 * @param person      the personal data of the associated person
 */
@Schema(description = "Tutor of a group, with the personal data of its person.")
public record TutorResponse(

        @Schema(description = "Identifier of the professor that tutors the group.", example = "1")
        Long professorId,

        @Schema(description = "Personal data of the person the tutor is associated with.")
        PersonSummaryResponse person) {

    /**
     * Adapts the application-layer view into the REST payload.
     *
     * @param view the assembled view, must not be null
     * @return the response payload
     */
    public static TutorResponse from(GroupDetailView.TutorView view) {
        return new TutorResponse(
                view.professor().id().value(),
                PersonSummaryResponse.from(view.person()));
    }
}
