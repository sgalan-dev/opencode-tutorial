package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreateCourseUseCase;
import com.example.opencodetutorial.application.usecase.DeleteCourseUseCase;
import com.example.opencodetutorial.application.usecase.GetCourseUseCase;
import com.example.opencodetutorial.application.usecase.ListCoursesUseCase;
import com.example.opencodetutorial.application.usecase.UpdateCourseUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.CourseRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.CourseResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.CourseRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Course}. Every
 * operation is nested under the center that owns the course; a course reachable through
 * a center that does not own it is reported as 404.
 */
@RestController
@RequestMapping("/api/v1/centers/{centerId}/courses")
@Tag(name = "Courses", description = "Operations over the Course aggregate, nested under the owning center.")
public class CourseController {

    private final CreateCourseUseCase createCourseUseCase;
    private final ListCoursesUseCase listCoursesUseCase;
    private final GetCourseUseCase getCourseUseCase;
    private final UpdateCourseUseCase updateCourseUseCase;
    private final DeleteCourseUseCase deleteCourseUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public CourseController(CreateCourseUseCase createCourseUseCase,
                            ListCoursesUseCase listCoursesUseCase,
                            GetCourseUseCase getCourseUseCase,
                            UpdateCourseUseCase updateCourseUseCase,
                            DeleteCourseUseCase deleteCourseUseCase) {
        this.createCourseUseCase = createCourseUseCase;
        this.listCoursesUseCase = listCoursesUseCase;
        this.getCourseUseCase = getCourseUseCase;
        this.updateCourseUseCase = updateCourseUseCase;
        this.deleteCourseUseCase = deleteCourseUseCase;
    }

    /**
     * Lists every course of a center.
     */
    @Operation(
            summary = "List the courses of a center",
            description = "Returns every course belonging to the center, in no particular order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of courses (possibly empty).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CourseResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<List<CourseResponse>> list(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId) {
        List<CourseResponse> body = listCoursesUseCase.execute(new CenterId(centerId)).stream()
                .map(CourseResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a course by its identifier.
     */
    @Operation(
            summary = "Get a course by id",
            description = "Returns the course with the given identifier when the center owns it, or 404 otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content)})
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> get(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the course to retrieve.", example = "1", required = true)
            @PathVariable Long courseId) {
        Course course = getCourseUseCase.execute(new CenterId(centerId), new CourseId(courseId));
        return ResponseEntity.ok(CourseResponse.from(course));
    }

    /**
     * Creates a new course inside a center.
     */
    @Operation(
            summary = "Create a new course",
            description = "Persists a new course under the given center and returns its identifier in the "
                    + "`Location` header. The name must be unique inside that center.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created course.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The center already has a course with that name.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<CourseResponse> create(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @RequestBody(description = "Course to create. `name` is required and non-blank.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CourseRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CourseRequest request) {
        CourseId createdId = createCourseUseCase.execute(
                CourseRequestMapper.toCreateCommand(centerId, request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new CourseResponse(createdId.value(), centerId, request.name()));
    }

    /**
     * Replaces the course with the given identifier.
     */
    @Operation(
            summary = "Replace a course",
            description = "Replaces the course with the given identifier with the values from the request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CourseResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The center already has a course with that name.",
                    content = @Content)})
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> update(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the course to replace.", example = "1", required = true)
            @PathVariable Long courseId,
            @RequestBody(description = "Replacement payload for the course.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CourseRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CourseRequest request) {
        Course updated = updateCourseUseCase.execute(
                CourseRequestMapper.toUpdateCommand(centerId, courseId, request));
        return ResponseEntity.ok(CourseResponse.from(updated));
    }

    /**
     * Removes the course with the given identifier.
     */
    @Operation(
            summary = "Delete a course",
            description = "Removes the course with the given identifier. Refused with 409 while it still owns groups.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center, or the course does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The course still has dependent groups.",
                    content = @Content)})
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the course to delete.", example = "1", required = true)
            @PathVariable Long courseId) {
        deleteCourseUseCase.execute(new CenterId(centerId), new CourseId(courseId));
        return ResponseEntity.noContent().build();
    }
}