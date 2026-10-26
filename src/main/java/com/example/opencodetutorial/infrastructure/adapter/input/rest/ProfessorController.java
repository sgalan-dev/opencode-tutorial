package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreateProfessorUseCase;
import com.example.opencodetutorial.application.usecase.DeleteProfessorUseCase;
import com.example.opencodetutorial.application.usecase.GetProfessorDetailUseCase;
import com.example.opencodetutorial.application.usecase.GetProfessorUseCase;
import com.example.opencodetutorial.application.usecase.ListProfessorsUseCase;
import com.example.opencodetutorial.application.usecase.UpdateProfessorUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.ProfessorDetailResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.ProfessorRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.ProfessorResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.ProfessorRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Professor}, nested
 * under the center it belongs to. A professor is the association of a person with a
 * center, so every route carries the center of the professorship.
 */
@RestController
@RequestMapping("/api/v1/centers/{centerId}/professors")
@Tag(name = "Professors", description = "Operations over the Professor aggregate, nested under the owning center. "
        + "Persisted to the `professor` table.")
public class ProfessorController {

    private final CreateProfessorUseCase createProfessorUseCase;
    private final ListProfessorsUseCase listProfessorsUseCase;
    private final GetProfessorUseCase getProfessorUseCase;
    private final UpdateProfessorUseCase updateProfessorUseCase;
    private final DeleteProfessorUseCase deleteProfessorUseCase;
    private final GetProfessorDetailUseCase getProfessorDetailUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public ProfessorController(CreateProfessorUseCase createProfessorUseCase,
                               ListProfessorsUseCase listProfessorsUseCase,
                               GetProfessorUseCase getProfessorUseCase,
                               UpdateProfessorUseCase updateProfessorUseCase,
                               DeleteProfessorUseCase deleteProfessorUseCase,
                               GetProfessorDetailUseCase getProfessorDetailUseCase) {
        this.createProfessorUseCase = createProfessorUseCase;
        this.listProfessorsUseCase = listProfessorsUseCase;
        this.getProfessorUseCase = getProfessorUseCase;
        this.updateProfessorUseCase = updateProfessorUseCase;
        this.deleteProfessorUseCase = deleteProfessorUseCase;
        this.getProfessorDetailUseCase = getProfessorDetailUseCase;
    }

    /**
     * Lists every professor of a center.
     */
    @Operation(
            summary = "List the professors of a center",
            description = "Returns every professor belonging to the center, in no particular order. Each professor "
                    + "includes its center id and the id of the person it is associated with.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of professors (possibly empty).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProfessorResponse.class)))),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping
    public ResponseEntity<List<ProfessorResponse>> list(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId) {
        List<ProfessorResponse> body = listProfessorsUseCase.execute(new CenterId(centerId)).stream()
                .map(ProfessorResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a professor by its identifier.
     */
    @Operation(
            summary = "Get a professor by id",
            description = "Returns the professor with the given identifier when the center owns it, or 404 otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Professor found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center, or the professor does not belong to it.",
                    content = @Content)})
    @GetMapping("/{professorId}")
    public ResponseEntity<ProfessorResponse> get(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the professor to retrieve.", example = "1", required = true)
            @PathVariable Long professorId) {
        Professor professor = getProfessorUseCase.execute(
                new CenterId(centerId), new ProfessorId(professorId));
        return ResponseEntity.ok(ProfessorResponse.from(professor));
    }

    /**
     * Creates a new professor inside a center.
     */
    @Operation(
            summary = "Create a new professor",
            description = "Persists a new professor under the given center and returns its identifier in the "
                    + "`Location` header. The referenced person must exist, and it must not already be a "
                    + "professor of that center.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Professor created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created professor.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center or person.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The person is already a professor of that center.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<ProfessorResponse> create(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @RequestBody(description = "Professor to create. `personId` is required and must reference an existing person.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProfessorRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody ProfessorRequest request) {
        ProfessorId createdId = createProfessorUseCase.execute(
                ProfessorRequestMapper.toCreateCommand(centerId, request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new ProfessorResponse(createdId.value(), centerId, request.personId()));
    }

    /**
     * Replaces the professor with the given identifier.
     */
    @Operation(
            summary = "Replace a professor",
            description = "Replaces the person associated with the professor with the given identifier. The center "
                    + "never changes, and the new person must not already be a professor of that center.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Professor updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfessorResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center/professor/person.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The person is already a professor of that center.",
                    content = @Content)})
    @PutMapping("/{professorId}")
    public ResponseEntity<ProfessorResponse> update(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the professor to replace.", example = "1", required = true)
            @PathVariable Long professorId,
            @RequestBody(description = "Replacement payload for the professor.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProfessorRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody ProfessorRequest request) {
        Professor updated = updateProfessorUseCase.execute(
                ProfessorRequestMapper.toUpdateCommand(centerId, professorId, request));
        return ResponseEntity.ok(ProfessorResponse.from(updated));
    }

    /**
     * Removes the professor with the given identifier.
     */
    @Operation(
            summary = "Delete a professor",
            description = "Removes the professor with the given identifier. Refused with 409 while it still "
                    + "tutors at least one group.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Professor deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No such center, or the professor does not belong to it.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The professor still tutors at least one group.",
                    content = @Content)})
    @DeleteMapping("/{professorId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the professor to delete.", example = "1", required = true)
            @PathVariable Long professorId) {
        deleteProfessorUseCase.execute(new CenterId(centerId), new ProfessorId(professorId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the assembled detail of a professor.
     */
    @Operation(
            summary = "Get the detail of a professor",
            description = "Returns the professor, the center it belongs to and the personal data of the associated "
                    + "person. The groups tutored by the professor are not part of the response.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assembled professor detail.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfessorDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "No such center, or the professor does not belong to it.",
                    content = @Content)})
    @GetMapping("/{professorId}/detail")
    public ResponseEntity<ProfessorDetailResponse> detail(
            @Parameter(description = "Identifier of the owning center.", example = "1", required = true)
            @PathVariable Long centerId,
            @Parameter(description = "Identifier of the professor.", example = "1", required = true)
            @PathVariable Long professorId) {
        ProfessorDetailResponse body = ProfessorDetailResponse.from(
                getProfessorDetailUseCase.execute(new CenterId(centerId), new ProfessorId(professorId)));
        return ResponseEntity.ok(body);
    }
}
