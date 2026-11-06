package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response payload for {@code GET .../groups/{groupId}/students/detail}: the group, its
 * course, every enrolled student with the personal data of the associated person and,
 * when the group has one, its tutor with the personal data of the person behind it.
 *
 * @param group    the requested group
 * @param course   the course that owns the group
 * @param students the enrolled students, empty when the group has none
 * @param tutor    the tutor of the group, null when the group has none
 */
@Schema(description = "Assembled view of a group: the group, its course, its enrolled students and its tutor.")
public record GroupDetailResponse(

        @Schema(description = "The requested group.")
        GroupResponse group,

        @Schema(description = "The course that owns the group.")
        CourseResponse course,

        @Schema(description = "Students enrolled in the group, with their personal data.")
        List<EnrolledStudentResponse> students,

        @Schema(description = "Tutor of the group, with the personal data of its person. Null when the group "
                + "has no tutor.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true)
        TutorResponse tutor) {

    /**
     * Adapts the application-layer read model into the REST payload.
     *
     * @param view the assembled view, must not be null
     * @return the response payload
     */
    public static GroupDetailResponse from(GroupDetailView view) {
        return new GroupDetailResponse(
                GroupResponse.from(view.group()),
                CourseResponse.from(view.course()),
                view.students().stream()
                        .map(EnrolledStudentResponse::from)
                        .toList(),
                view.tutor() == null ? null : TutorResponse.from(view.tutor()));
    }
}
