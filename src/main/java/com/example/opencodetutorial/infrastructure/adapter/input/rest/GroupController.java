package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreateGroupUseCase;
import com.example.opencodetutorial.application.usecase.DeleteGroupUseCase;
import com.example.opencodetutorial.application.usecase.DetachStudentFromGroupUseCase;
import com.example.opencodetutorial.application.usecase.EnrollStudentInGroupUseCase;
import com.example.opencodetutorial.application.usecase.GetGroupDetailUseCase;
import com.example.opencodetutorial.application.usecase.GetGroupUseCase;
import com.example.opencodetutorial.application.usecase.ListGroupsUseCase;
import com.example.opencodetutorial.application.usecase.ListStudentsInGroupUseCase;
import com.example.opencodetutorial.application.usecase.UpdateGroupUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupDetailResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.StudentResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.GroupRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Group}, nested under
 * the course that owns it. It also hosts the enrolment and detachment of students, which
 * used to live under {@code /api/v1/students/{studentId}/groups/{groupId}} and are now
 * expressed as {@code .../groups/{groupId}/students/{studentId}}.
 */
@RestController
@RequestMapping("/api/v1/centers/{centerId}/courses/{courseId}/groups")
@Tag(name = "Groups", description = "Operations over the Group aggregate and its student enrolments. "
        + "Persisted to the `student_group` table.")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final ListGroupsUseCase listGroupsUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;
    private final ListStudentsInGroupUseCase listStudentsInGroupUseCase;
    private final GetGroupDetailUseCase getGroupDetailUseCase;
    private final EnrollStudentInGroupUseCase enrollStudentInGroupUseCase;
    private final DetachStudentFromGroupUseCase detachStudentFromGroupUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public GroupController(CreateGroupUseCase createGroupUseCase,
                           ListGroupsUseCase listGroupsUseCase,
                           GetGroupUseCase getGroupUseCase,
                           UpdateGroupUseCase updateGroupUseCase,
                           DeleteGroupUseCase deleteGroupUseCase,
                           ListStudentsInGroupUseCase listStudentsInGroupUseCase,
                           GetGroupDetailUseCase getGroupDetailUseCase,
                           EnrollStudentInGroupUseCase enrollStudentInGroupUseCase,
                           DetachStudentFromGroupUseCase detachStudentFromGroupUseCase) {
        this.createGroupUseCase = createGroupUseCase;
        this.listGroupsUseCase = listGroupsUseCase;
        this.getGroupUseCase = getGroupUseCase;
        this.updateGroupUseCase = updateGroupUseCase;
        this.deleteGroupUseCase = deleteGroupUseCase;
        this.listStudentsInGroupUseCase = listStudentsInGroupUseCase;
        this.getGroupDetailUseCase = getGroupDetailUseCase;
        this.enrollStudentInGroupUseCase = enrollStudentInGroupUseCase;
        this.detachStudentFromGroupUseCase = detachStudentFromGroupUseCase;
    }

    /**
     * Lists every group of a course.
     */
    @Operation(
            summary = "List the groups of a course",
            description = "Returns every group belonging to the course, in no particular order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of groups (possibly empty).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GroupResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<List<GroupResponse>> list(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId) {
        List<GroupResponse> body = listGroupsUseCase
                .execute(new CenterId(centerId), new CourseId(courseId)).stream()
                .map(GroupResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a group by its identifier.
     */
    @Operation(
            summary = "Get a group by id",
            description = "Returns the group with the given identifier when the course owns it, or 404 otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the group does not belong to it.",
                    content = @Content)})
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> get(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group to retrieve.", example = "1", required = true)
            @PathVariable Long groupId) {
        Group group = getGroupUseCase.execute(
                new CenterId(centerId), new CourseId(courseId), new GroupId(groupId));
        return ResponseEntity.ok(GroupResponse.from(group));
    }

    /**
     * Creates a new group inside a course.
     */
    @Operation(
            summary = "Create a new group",
            description = "Persists a new group under the given course and returns its identifier in the "
                    + "`Location` header. The name must be unique inside that course.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Group created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created group.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The course already has a group with that name.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<GroupResponse> create(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @RequestBody(description = "Group to create. `name` is required and non-blank.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GroupRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody GroupRequest request) {
        GroupId createdId = createGroupUseCase.execute(
                GroupRequestMapper.toCreateCommand(centerId, courseId, request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new GroupResponse(createdId.value(), courseId, request.name()));
    }

    /**
     * Replaces the group with the given identifier.
     */
    @Operation(
            summary = "Replace a group",
            description = "Replaces the name of the group with the given identifier, keeping it under the "
                    + "same course.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the group does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The course already has a group with that name.",
                    content = @Content)})
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> update(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group to replace.", example = "1", required = true)
            @PathVariable Long groupId,
            @RequestBody(description = "Replacement payload for the group.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GroupRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody GroupRequest request) {
        Group updated = updateGroupUseCase.execute(
                GroupRequestMapper.toUpdateCommand(centerId, courseId, groupId, request));
        return ResponseEntity.ok(GroupResponse.from(updated));
    }

    /**
     * Removes the group with the given identifier and all its enrolments.
     */
    @Operation(
            summary = "Delete a group",
            description = "Removes the group with the given identifier together with every student enrolment "
                    + "that targeted it.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Group deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the group does not belong to it.",
                    content = @Content)})
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group to delete.", example = "1", required = true)
            @PathVariable Long groupId) {
        deleteGroupUseCase.execute(new CenterId(centerId), new CourseId(courseId), new GroupId(groupId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Lists the students enrolled in a group.
     */
    @Operation(
            summary = "List the students of a group",
            description = "Returns every student currently enrolled in the group, in no particular order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of students (possibly empty).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = StudentResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the group does not belong to it.",
                    content = @Content)})
    @GetMapping("/{groupId}/students")
    public ResponseEntity<List<StudentResponse>> listStudents(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group.", example = "1", required = true)
            @PathVariable Long groupId) {
        List<StudentResponse> body = listStudentsInGroupUseCase
                .execute(new CenterId(centerId), new CourseId(courseId), new GroupId(groupId)).stream()
                .map(StudentResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves the assembled detail of a group.
     */
    @Operation(
            summary = "Get the detail of a group",
            description = "Returns the group, the course it belongs to and every enrolled student with the "
                    + "personal data of the associated person.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assembled group detail.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center/course, or the group does not belong to it.",
                    content = @Content)})
    @GetMapping("/{groupId}/students/detail")
    public ResponseEntity<GroupDetailResponse> detail(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group.", example = "1", required = true)
            @PathVariable Long groupId) {
        GroupDetailResponse body = GroupDetailResponse.from(
                getGroupDetailUseCase.execute(new CenterId(centerId), new CourseId(courseId), new GroupId(groupId)));
        return ResponseEntity.ok(body);
    }

    /**
     * Enrols the given student in the given group.
     */
    @Operation(
            summary = "Enrol a student in a group",
            description = "Adds the group to the student's enrolment set. Idempotent: enrolling a student that "
                    + "is already in the group is a no-op. A student can hold at most one group per course, so "
                    + "enrolling them in a second group of the same course is rejected with 409.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Enrolment applied (or already present). No content.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Any element of the hierarchy does not exist, "
                    + "or the student does not belong to the center in the URL.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The student is already enrolled in another group "
                    + "of the same course.",
                    content = @Content)})
    @PostMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<Void> enroll(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group to enrol the student in.", example = "1", required = true)
            @PathVariable Long groupId,
            @Parameter(description = "Identifier of the student to enrol.", example = "1", required = true)
            @PathVariable Long studentId) {
        enrollStudentInGroupUseCase.execute(StudentRequestMapper.toEnrollCommand(
                centerId, courseId, groupId, studentId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes the given student from the given group.
     */
    @Operation(
            summary = "Remove a student from a group",
            description = "Removes the group from the student's enrolment set. Idempotent: removing a student "
                    + "that is not in the group is a no-op.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Detachment applied (or already absent). No content.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Any element of the hierarchy does not exist, "
                    + "or the student does not belong to the center in the URL.",
                    content = @Content)})
    @DeleteMapping("/{groupId}/students/{studentId}")
    public ResponseEntity<Void> detach(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the owning course.", example = "1", required = true)
            @PathVariable Long courseId,
            @Parameter(description = "Identifier of the group to remove the student from.", example = "1", required = true)
            @PathVariable Long groupId,
            @Parameter(description = "Identifier of the student to remove.", example = "1", required = true)
            @PathVariable Long studentId) {
        detachStudentFromGroupUseCase.execute(StudentRequestMapper.toDetachCommand(
                centerId, courseId, groupId, studentId));
        return ResponseEntity.noContent().build();
    }
}