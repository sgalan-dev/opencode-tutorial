package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreateStudentUseCase;
import com.example.opencodetutorial.application.usecase.DeleteStudentUseCase;
import com.example.opencodetutorial.application.usecase.GetStudentDetailUseCase;
import com.example.opencodetutorial.application.usecase.GetStudentUseCase;
import com.example.opencodetutorial.application.usecase.ListStudentsUseCase;
import com.example.opencodetutorial.application.usecase.UpdateStudentUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentDetailResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.StudentRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Student}, nested under
 * the center it belongs to. Enrolment and detachment moved to
 * {@link GroupController}, under the group that receives the student.
 */
@RestController
@RequestMapping("/api/v1/centers/{centerId}/students")
@Tag(name = "Students", description = "Operations over the Student aggregate, nested under the owning center.")
public class StudentController {

    private final CreateStudentUseCase createStudentUseCase;
    private final ListStudentsUseCase listStudentsUseCase;
    private final GetStudentUseCase getStudentUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;
    private final DeleteStudentUseCase deleteStudentUseCase;
    private final GetStudentDetailUseCase getStudentDetailUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public StudentController(CreateStudentUseCase createStudentUseCase,
                             ListStudentsUseCase listStudentsUseCase,
                             GetStudentUseCase getStudentUseCase,
                             UpdateStudentUseCase updateStudentUseCase,
                             DeleteStudentUseCase deleteStudentUseCase,
                             GetStudentDetailUseCase getStudentDetailUseCase) {
        this.createStudentUseCase = createStudentUseCase;
        this.listStudentsUseCase = listStudentsUseCase;
        this.getStudentUseCase = getStudentUseCase;
        this.updateStudentUseCase = updateStudentUseCase;
        this.deleteStudentUseCase = deleteStudentUseCase;
        this.getStudentDetailUseCase = getStudentDetailUseCase;
    }

    /**
     * Lists every student of a center.
     */
    @Operation(
            summary = "List the students of a center",
            description = "Returns every student belonging to the center, in no particular order. Each student "
                    + "includes its center id, its associated person id and the ids of the groups it is enrolled in.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of students (possibly empty).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = StudentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<List<StudentResponse>> list(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId) {
        List<StudentResponse> body = listStudentsUseCase.execute(new CenterId(centerId)).stream()
                .map(StudentResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a student by its identifier.
     */
    @Operation(
            summary = "Get a student by id",
            description = "Returns the student with the given identifier when the center owns it, or 404 otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center, or the student does not belong to it.",
                    content = @Content)})
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> get(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the student to retrieve.", example = "1", required = true)
            @PathVariable Long studentId) {
        Student student = getStudentUseCase.execute(new CenterId(centerId), new StudentId(studentId));
        return ResponseEntity.ok(StudentResponse.from(student));
    }

    /**
     * Creates a new student inside a center.
     */
    @Operation(
            summary = "Create a new student",
            description = "Persists a new student associated with the supplied person and optionally enrolled in "
                    + "the supplied groups. Every group must belong to the center in the URL.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created student.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center or person, or one of the groups does "
                    + "not belong to the center.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Two of the supplied groups share the same course.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<StudentResponse> create(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @RequestBody(description = "Student to create. `personId` is required and must reference an existing person.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StudentRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody StudentRequest request) {
        StudentId createdId = createStudentUseCase.execute(
                StudentRequestMapper.toCreateCommand(centerId, request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new StudentResponse(createdId.value(), centerId, request.personId(),
                        request.groupIds() == null ? java.util.Set.of() : request.groupIds()));
    }

    /**
     * Replaces the student with the given identifier.
     */
    @Operation(
            summary = "Replace a student",
            description = "Replaces the student with the given identifier. The person association and the full "
                    + "set of enrolments are replaced atomically; sending a different `centerId` transfers the "
                    + "student to that center, and the supplied groups must belong to it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center/student/person, or one of the groups "
                    + "does not belong to the target center.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Two of the supplied groups share the same course.",
                    content = @Content)})
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> update(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the student to replace.", example = "1", required = true)
            @PathVariable Long studentId,
            @RequestBody(description = "Replacement payload for the student.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StudentRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody StudentRequest request) {
        Student updated = updateStudentUseCase.execute(
                StudentRequestMapper.toUpdateCommand(centerId, studentId, request));
        return ResponseEntity.ok(StudentResponse.from(updated));
    }

    /**
     * Removes the student with the given identifier and all its enrolments.
     */
    @Operation(
            summary = "Delete a student",
            description = "Removes the student with the given identifier together with every enrolment it holds.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Student deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center, or the student does not belong to it.",
                    content = @Content)})
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the student to delete.", example = "1", required = true)
            @PathVariable Long studentId) {
        deleteStudentUseCase.execute(new CenterId(centerId), new StudentId(studentId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the assembled detail of a student.
     */
    @Operation(
            summary = "Get the detail of a student",
            description = "Returns the student, the center it belongs to, the personal data of the associated "
                    + "person and every enrolment enriched with the group and its course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assembled student detail.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StudentDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center, or the student does not belong to it.",
                    content = @Content)})
    @GetMapping("/{studentId}/detail")
    public ResponseEntity<StudentDetailResponse> detail(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the student.", example = "1", required = true)
            @PathVariable Long studentId) {
        StudentDetailResponse body = StudentDetailResponse.from(
                getStudentDetailUseCase.execute(new CenterId(centerId), new StudentId(studentId)));
        return ResponseEntity.ok(body);
    }
}