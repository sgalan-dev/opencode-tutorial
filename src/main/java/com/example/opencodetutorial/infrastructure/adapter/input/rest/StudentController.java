package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.AssignStudentToGroupUseCase;
import com.example.opencodetutorial.application.usecase.CreateStudentUseCase;
import com.example.opencodetutorial.application.usecase.DeleteStudentUseCase;
import com.example.opencodetutorial.application.usecase.DetachStudentFromGroupUseCase;
import com.example.opencodetutorial.application.usecase.GetStudentUseCase;
import com.example.opencodetutorial.application.usecase.ListStudentsUseCase;
import com.example.opencodetutorial.application.usecase.UpdateStudentUseCase;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.StudentRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Student}.
 * Exposes the standard CRUD endpoints plus two nested endpoints to enrol a
 * student in a group and to detach them again.
 */
@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students",
        description = "Operations over the Student aggregate, including enrolment in and detachment from groups.")
public class StudentController {

    private final CreateStudentUseCase createStudentUseCase;
    private final ListStudentsUseCase listStudentsUseCase;
    private final GetStudentUseCase getStudentUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;
    private final DeleteStudentUseCase deleteStudentUseCase;
    private final AssignStudentToGroupUseCase assignStudentToGroupUseCase;
    private final DetachStudentFromGroupUseCase detachStudentFromGroupUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public StudentController(CreateStudentUseCase createStudentUseCase,
                             ListStudentsUseCase listStudentsUseCase,
                             GetStudentUseCase getStudentUseCase,
                             UpdateStudentUseCase updateStudentUseCase,
                             DeleteStudentUseCase deleteStudentUseCase,
                             AssignStudentToGroupUseCase assignStudentToGroupUseCase,
                             DetachStudentFromGroupUseCase detachStudentFromGroupUseCase) {
        this.createStudentUseCase = createStudentUseCase;
        this.listStudentsUseCase = listStudentsUseCase;
        this.getStudentUseCase = getStudentUseCase;
        this.updateStudentUseCase = updateStudentUseCase;
        this.deleteStudentUseCase = deleteStudentUseCase;
        this.assignStudentToGroupUseCase = assignStudentToGroupUseCase;
        this.detachStudentFromGroupUseCase = detachStudentFromGroupUseCase;
    }

    /**
     * Lists every student.
     */
    @Operation(
            summary = "List all students",
            description = "Returns every persisted student, in no particular order. Each student includes its associated person id and the ids of the groups it is enrolled in.")
    @ApiResponse(responseCode = "200", description = "List of students (possibly empty).",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = StudentResponse.class)))
    @GetMapping
    public ResponseEntity<List<StudentResponse>> list() {
        List<StudentResponse> body = listStudentsUseCase.execute().stream()
                .map(StudentResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a student by its identifier.
     */
    @Operation(
            summary = "Get a student by id",
            description = "Returns the student with the given identifier, or 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "404", description = "No student exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> get(
            @Parameter(description = "Identifier of the student to retrieve.", example = "1", required = true)
            @PathVariable Long id) {
        Student student = getStudentUseCase.execute(new StudentId(id));
        return ResponseEntity.ok(StudentResponse.from(student));
    }

    /**
     * Creates a new student.
     */
    @Operation(
            summary = "Create a new student",
            description = "Persists a new student associated with the supplied person and optionally enrolled in the supplied groups.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created student.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<StudentResponse> create(
            @RequestBody(description = "Student to create. `personId` is required and must reference an existing person.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StudentRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody StudentRequest request) {
        StudentId createdId = createStudentUseCase.execute(StudentRequestMapper.toCreateCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new StudentResponse(createdId.value(), request.personId(), request.groupIds()));
    }

    /**
     * Replaces the student with the given identifier.
     */
    @Operation(
            summary = "Replace a student",
            description = "Replaces the student with the given identifier. Both the person association and the full set of group enrolments are replaced atomically.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No student exists with the supplied identifier.",
                    content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
            @Parameter(description = "Identifier of the student to replace.", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Replacement payload for the student.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StudentRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody StudentRequest request) {
        Student updated = updateStudentUseCase.execute(StudentRequestMapper.toUpdateCommand(id, request));
        return ResponseEntity.ok(StudentResponse.from(updated));
    }

    /**
     * Removes the student with the given identifier.
     */
    @Operation(
            summary = "Delete a student",
            description = "Removes the student with the given identifier. Returns 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Student deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No student exists with the supplied identifier.",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the student to delete.", example = "1", required = true)
            @PathVariable Long id) {
        deleteStudentUseCase.execute(new StudentId(id));
        return ResponseEntity.noContent().build();
    }

    /**
     * Enrols the given student in the given group.
     */
    @Operation(
            summary = "Enrol a student in a group",
            description = "Adds the given group to the student's enrolment set. Idempotent: enrolling a student that is already in the group is a no-op.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Enrolment applied (or already present). No content.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Either the student or the group does not exist.",
                    content = @Content)})
    @PostMapping("/{studentId}/groups/{groupId}")
    public ResponseEntity<Void> assign(
            @Parameter(description = "Identifier of the student to enrol.", example = "1", required = true)
            @PathVariable Long studentId,
            @Parameter(description = "Identifier of the group to enrol the student in.", example = "2", required = true)
            @PathVariable Long groupId) {
        assignStudentToGroupUseCase.execute(StudentRequestMapper.toAssignCommand(studentId, groupId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes the given student from the given group.
     */
    @Operation(
            summary = "Remove a student from a group",
            description = "Removes the given group from the student's enrolment set. Idempotent: removing a student that is not in the group is a no-op.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Detachment applied (or already absent). No content.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "The student does not exist.",
                    content = @Content)})
    @DeleteMapping("/{studentId}/groups/{groupId}")
    public ResponseEntity<Void> detach(
            @Parameter(description = "Identifier of the student to remove from the group.", example = "1", required = true)
            @PathVariable Long studentId,
            @Parameter(description = "Identifier of the group to remove the student from.", example = "2", required = true)
            @PathVariable Long groupId) {
        detachStudentFromGroupUseCase.execute(StudentRequestMapper.toDetachCommand(studentId, groupId));
        return ResponseEntity.noContent().build();
    }
}
