package com.example.opencodetutorial.infrastructure.adapter.input.rest.dto;

import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.model.Subject;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload for subject-related endpoints.
 *
 * @param id          the subject's identifier
 * @param courseId    the identifier of the course that owns the subject
 * @param name        the subject's display name
 * @param professorId the identifier of the professor that teaches the subject, or
 *                    {@code null} when the subject has no professor
 */
@Schema(description = "Subject resource as returned by the API. Persisted to the `subject` and "
        + "`subject_teaching` tables.")
public record SubjectResponse(

        @Schema(description = "Auto-generated identifier of the subject.",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @Schema(description = "Identifier of the course that owns the subject.", example = "1")
        Long courseId,

        @Schema(description = "Display name of the subject.", example = "Matemáticas")
        String name,

        @Schema(description = "Identifier of the professor that teaches the subject. Null when the subject "
                + "has no professor.",
                example = "1",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true)
        Long professorId) {

    /**
     * Adapts a domain {@link Subject} aggregate into a {@link SubjectResponse}.
     *
     * @param subject the source aggregate, must not be null
     * @return the response payload
     */
    public static SubjectResponse from(Subject subject) {
        ProfessorId professor = subject.professorId();
        return new SubjectResponse(
                subject.id().value(),
                subject.courseId().value(),
                subject.name().value(),
                professor == null ? null : professor.value());
    }
}
