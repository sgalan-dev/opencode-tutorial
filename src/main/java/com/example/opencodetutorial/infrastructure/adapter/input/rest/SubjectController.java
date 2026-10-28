package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.AssignProfessorToSubjectUseCase;
import com.example.opencodetutorial.application.usecase.CreateSubjectUseCase;
import com.example.opencodetutorial.application.usecase.DeleteSubjectUseCase;
import com.example.opencodetutorial.application.usecase.GetSubjectUseCase;
import com.example.opencodetutorial.application.usecase.ListSubjectsUseCase;
import com.example.opencodetutorial.application.usecase.UnassignProfessorFromSubjectUseCase;
import com.example.opencodetutorial.application.usecase.UpdateSubjectUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.SubjectRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.SubjectResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.SubjectRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Subject}, nested under
 * the course that owns it. It also hosts the assignment and the removal of the
 * professor that teaches the subject, which live in their own endpoints so the
 * {@code PUT} of the subject never touches them.
 */
@RestController
@RequestMapping("/api/v1/centers/{centerId}/courses/{courseId}/subjects")
@Tag(name = "Subjects", description = "Operations over the Subject aggregate and the professor that teaches it. "
        + "Persisted to the `subject` and `subject_teaching` tables.")
public class SubjectController {

    private final CreateSubjectUseCase createSubjectUseCase;
    private final ListSubjectsUseCase listSubjectsUseCase;
    private final GetSubjectUseCase getSubjectUseCase;
    private final UpdateSubjectUseCase updateSubjectUseCase;
    private final DeleteSubjectUseCase deleteSubjectUseCase;
    private final AssignProfessorToSubjectUseCase assignProfessorToSubjectUseCase;
    private final UnassignProfessorFromSubjectUseCase unassignProfessorFromSubjectUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public SubjectController(CreateSubjectUseCase createSubjectUseCase,
                             ListSubjectsUseCase listSubjectsUseCase,
                             GetSubjectUseCase getSubjectUseCase,
                             UpdateSubjectUseCase updateSubjectUseCase,
                             DeleteSubjectUseCase deleteSubjectUseCase,
                             AssignProfessorToSubjectUseCase assignProfessorToSubjectUseCase,
                             UnassignProfessorFromSubjectUseCase unassignProfessorFromSubjectUseCase) {
        this.createSubjectUseCase = createSubjectUseCase;
        this.listSubjectsUseCase = listSubjectsUseCase;
        this.getSubjectUseCase = getSubjectUseCase;
        this.updateSubjectUseCase = updateSubjectUseCase;
        this.deleteSubjectUseCase = deleteSubjectUseCase;
        this.assignProfessorToSubjectUseCase = assignProfessorToSubjectUseCase;
        this.unassignProfessorFromSubjectUseCase = unassignProfessorFromSubjectUseCase;
    }

    /**
     * Lists every subject of a course.
     */
    @Operation(
            summary = "List the subjects of a course",
            description = "Returns every subject belonging to the course, in no particular order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of subjects (possibly empty).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SubjectResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> list(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId) {
        List<SubjectResponse> body = listSubjectsUseCase
                .execute(new CenterId(centerId), new CourseId(courseId)).stream()
                .map(SubjectResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a subject by its identifier.
     */
    @Operation(
            summary = "Get a subject by id",
            description = "Returns the subject with the given identifier when the course owns it, or 404 otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subject found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the subject does not belong to it.",
                    content = @Content)})
    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> get(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the subject to retrieve.", example = "1", required = true)
            @PathVariable Long subjectId) {
        Subject subject = getSubjectUseCase.execute(
                new CenterId(centerId), new CourseId(courseId), new SubjectId(subjectId));
        return ResponseEntity.ok(SubjectResponse.from(subject));
    }

    /**
     * Creates a new subject inside a course.
     */
    @Operation(
            summary = "Create a new subject",
            description = "Persists a new subject under the given course and returns its identifier in the "
                    + "`Location` header. The name must be unique inside that course. The subject is created "
                    + "without a professor: the assignment has its own endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subject created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created subject.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The course already has a subject with that name.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<SubjectResponse> create(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @RequestBody(description = "Subject to create. `name` is required and non-blank.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SubjectRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody SubjectRequest request) {
        SubjectId createdId = createSubjectUseCase.execute(
                SubjectRequestMapper.toCreateCommand(centerId, courseId, request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new SubjectResponse(createdId.value(), courseId, request.name(), null));
    }

    /**
     * Replaces the subject with the given identifier.
     */
    @Operation(
            summary = "Replace a subject",
            description = "Replaces the name of the subject with the given identifier, keeping it under the "
                    + "same course and keeping the professor that teaches it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subject updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the subject does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The course already has a subject with that name.",
                    content = @Content)})
    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectResponse> update(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the subject to replace.", example = "1", required = true)
            @PathVariable Long subjectId,
            @RequestBody(description = "Replacement payload for the subject.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SubjectRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody SubjectRequest request) {
        Subject updated = updateSubjectUseCase.execute(
                SubjectRequestMapper.toUpdateCommand(centerId, courseId, subjectId, request));
        return ResponseEntity.ok(SubjectResponse.from(updated));
    }

    /**
     * Removes the subject with the given identifier.
     */
    @Operation(
            summary = "Delete a subject",
            description = "Removes the subject with the given identifier together with the teaching assignment "
                    + "that targeted it.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subject deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the subject does not belong to it.",
                    content = @Content)})
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the subject to delete.", example = "1", required = true)
            @PathVariable Long subjectId) {
        deleteSubjectUseCase.execute(new CenterId(centerId), new CourseId(courseId), new SubjectId(subjectId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigns the given professor as the teacher of the given subject.
     */
    @Operation(
            summary = "Assign a professor to a subject",
            description = "Makes the given professor the teacher of the subject, replacing the professor it had, "
                    + "if any. The professor must be a professor of the center that owns the subject's course. "
                    + "Idempotent: assigning the professor the subject already has is a no-op. The `PUT` of the "
                    + "subject never touches the professor.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Professor assigned (or already present). No content.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Any element of the hierarchy does not exist, "
                    + "or the professor does not belong to the center in the URL.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The professor belongs to another center than the "
                    + "one that owns the subject's course.",
                    content = @Content)})
    @PostMapping("/{subjectId}/professor/{professorId}")
    public ResponseEntity<Void> assignProfessor(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the subject to assign the professor to.", example = "1", required = true)
            @PathVariable Long subjectId,
            @Parameter(description = "Identifier of the professor that becomes the teacher.", example = "1", required = true)
            @PathVariable Long professorId) {
        assignProfessorToSubjectUseCase.execute(SubjectRequestMapper.toAssignProfessorCommand(
                centerId, courseId, subjectId, professorId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes the professor that teaches the given subject.
     */
    @Operation(
            summary = "Remove the professor from a subject",
            description = "Removes the professor that teaches the subject. Idempotent: removing a professor that "
                    + "is not the current one is a no-op.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Professor removed (or already absent). No content.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Any element of the hierarchy does not exist, "
                    + "or the professor does not belong to the center in the URL.",
                    content = @Content)})
    @DeleteMapping("/{subjectId}/professor/{professorId}")
    public ResponseEntity<Void> unassignProfessor(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the subject to remove the professor from.", example = "1", required = true)
            @PathVariable Long subjectId,
            @Parameter(description = "Identifier of the professor that is expected to be the teacher.",
                    example = "1", required = true)
            @PathVariable Long professorId) {
        unassignProfessorFromSubjectUseCase.execute(SubjectRequestMapper.toUnassignProfessorCommand(
                centerId, courseId, subjectId, professorId));
        return ResponseEntity.noContent().build();
    }
}
