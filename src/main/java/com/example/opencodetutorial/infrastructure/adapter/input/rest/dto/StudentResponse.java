package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Response payload for student-related endpoints.
 *
 * @param id       the student's identifier
 * @param personId the associated person's identifier
 * @param groupIds the identifiers of the groups the student is enrolled in
 */
@Schema(description = "Student resource as returned by the API.")
public record StudentResponse(

        @Schema(description = "Auto-generated identifier of the student.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Identifier of the person the student is associated with.",
                example = "1")
        Long personId,

        @Schema(description = "Identifiers of the groups the student is enrolled in.",
                example = "[1, 2]")
        Set<Long> groupIds) {

    /**
     * Adapts a domain {@link Student} aggregate into a {@link StudentResponse}.
     *
     * @param student the source aggregate, must not be null
     * @return the response payload
     */
    public static StudentResponse from(Student student) {
        Set<Long> groupIds = student.groupIds().stream()
                .map(GroupId::value)
                .collect(Collectors.toUnmodifiableSet());
        return new StudentResponse(student.id().value(), student.personId().value(), groupIds);
    }
}
