package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.ProfessorDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for {@code GET .../professors/{professorId}/detail}: the professor,
 * the center it belongs to and the personal data of the associated person.
 *
 * @param professor the requested professor
 * @param center    the center the professor belongs to
 * @param person    the personal data of the associated person
 */
@Schema(description = "Assembled view of a professor: the professor, its center and its person.")
public record ProfessorDetailResponse(

        @Schema(description = "The requested professor.")
        ProfessorResponse professor,

        @Schema(description = "The center the professor belongs to.")
        CenterResponse center,

        @Schema(description = "Personal data of the person the professor is associated with.")
        PersonSummaryResponse person) {

    /**
     * Adapts the application-layer read model into the REST payload.
     *
     * @param view the assembled view, must not be null
     * @return the response payload
     */
    public static ProfessorDetailResponse from(ProfessorDetailView view) {
        return new ProfessorDetailResponse(
                ProfessorResponse.from(view.professor()),
                CenterResponse.from(view.center()),
                PersonSummaryResponse.from(view.person()));
    }
}
