package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response payload for {@code GET .../groups/{groupId}/students/detail}: the group, its
 * course and every enrolled student with the personal data of the associated person.
 *
 * @param group    the requested group
 * @param course   the course that owns the group
 * @param students the enrolled students, empty when the group has none
 */
@Schema(description = "Assembled view of a group: the group, its course and its enrolled students.")
public record GroupDetailResponse(

        @Schema(description = "The requested group.")
        GroupResponse group,

        @Schema(description = "The course that owns the group.")
        CourseResponse course,

        @Schema(description = "Students enrolled in the group, with their personal data.")
        List<EnrolledStudentResponse> students) {

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
                        .toList());
    }
}