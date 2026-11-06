package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.StudentDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response payload for {@code GET .../students/{studentId}/detail}: the student, the
 * center it belongs to, the personal data of its person and every enrolment enriched
 * with the group and the course.
 *
 * @param student    the requested student
 * @param center     the center the student belongs to
 * @param person     the personal data of the associated person
 * @param enrolments every enrolment, empty when the student is not enrolled anywhere
 */
@Schema(description = "Assembled view of a student: the student, its center, its person and its enrolments.")
public record StudentDetailResponse(

        @Schema(description = "The requested student.")
        StudentResponse student,

        @Schema(description = "The center the student belongs to.")
        CenterResponse center,

        @Schema(description = "Personal data of the person the student is associated with.")
        PersonSummaryResponse person,

        @Schema(description = "Enrolments of the student, each with its group and course.")
        List<EnrolmentResponse> enrolments) {

    /**
     * Adapts the application-layer read model into the REST payload.
     *
     * @param view the assembled view, must not be null
     * @return the response payload
     */
    public static StudentDetailResponse from(StudentDetailView view) {
        return new StudentDetailResponse(
                StudentResponse.from(view.student()),
                CenterResponse.from(view.center()),
                PersonSummaryResponse.from(view.person()),
                view.enrolments().stream()
                        .map(EnrolmentResponse::from)
                        .toList());
    }
}