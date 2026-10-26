package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreateCenterUseCase;
import com.example.opencodetutorial.application.usecase.DeleteCenterUseCase;
import com.example.opencodetutorial.application.usecase.GetCenterUseCase;
import com.example.opencodetutorial.application.usecase.ListCentersUseCase;
import com.example.opencodetutorial.application.usecase.UpdateCenterUseCase;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.CenterRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.CenterResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.CenterRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Center}, the root of
 * the Center -&gt; Course -&gt; Group -&gt; Student hierarchy. The center collection is the
 * only resource in the API that lives outside the hierarchy.
 */
@RestController
@RequestMapping("/api/v1/centers")
@Tag(name = "Centers", description = "Operations over the Center aggregate, the root of the hierarchy.")
public class CenterController {

    private final CreateCenterUseCase createCenterUseCase;
    private final ListCentersUseCase listCentersUseCase;
    private final GetCenterUseCase getCenterUseCase;
    private final UpdateCenterUseCase updateCenterUseCase;
    private final DeleteCenterUseCase deleteCenterUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public CenterController(CreateCenterUseCase createCenterUseCase,
                            ListCentersUseCase listCentersUseCase,
                            GetCenterUseCase getCenterUseCase,
                            UpdateCenterUseCase updateCenterUseCase,
                            DeleteCenterUseCase deleteCenterUseCase) {
        this.createCenterUseCase = createCenterUseCase;
        this.listCentersUseCase = listCentersUseCase;
        this.getCenterUseCase = getCenterUseCase;
        this.updateCenterUseCase = updateCenterUseCase;
        this.deleteCenterUseCase = deleteCenterUseCase;
    }

    /**
     * Lists every center.
     */
    @Operation(
            summary = "List all centers",
            description = "Returns every persisted center, in no particular order.")
    @ApiResponse(responseCode = "200", description = "List of centers (possibly empty).",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                            schema = @Schema(implementation = CenterResponse.class))))
    @GetMapping
    public ResponseEntity<List<CenterResponse>> list() {
        List<CenterResponse> body = listCentersUseCase.execute().stream()
                .map(CenterResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a center by its identifier.
     */
    @Operation(
            summary = "Get a center by id",
            description = "Returns the center with the given identifier, or 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Center found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CenterResponse.class))),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping("/{centerId}")
    public ResponseEntity<CenterResponse> get(
            @Parameter(description = "Identifier of the center to retrieve.", example = "1", required = true)
            @PathVariable Long centerId) {
        Center center = getCenterUseCase.execute(new CenterId(centerId));
        return ResponseEntity.ok(CenterResponse.from(center));
    }

    /**
     * Creates a new center.
     */
    @Operation(
            summary = "Create a new center",
            description = "Persists a new center and returns its identifier in the `Location` header. "
                    + "The name must be unique across all centers.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Center created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created center.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CenterResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Another center already uses the supplied name.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<CenterResponse> create(
            @RequestBody(description = "Center to create. `name` is required and non-blank.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CenterRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CenterRequest request) {
        CenterId createdId = createCenterUseCase.execute(CenterRequestMapper.toCreateCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new CenterResponse(createdId.value(), request.name()));
    }

    /**
     * Replaces the center with the given identifier.
     */
    @Operation(
            summary = "Replace a center",
            description = "Replaces the center with the given identifier with the values from the request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Center updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CenterResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Another center already uses the supplied name.",
                    content = @Content)})
    @PutMapping("/{centerId}")
    public ResponseEntity<CenterResponse> update(
            @Parameter(description = "Identifier of the center to replace.", example = "1", required = true)
            @PathVariable Long centerId,
            @RequestBody(description = "Replacement payload for the center.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CenterRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CenterRequest request) {
        Center updated = updateCenterUseCase.execute(
                CenterRequestMapper.toUpdateCommand(centerId, request));
        return ResponseEntity.ok(CenterResponse.from(updated));
    }

    /**
     * Removes the center with the given identifier.
     */
    @Operation(
            summary = "Delete a center",
            description = "Removes the center with the given identifier. Refused with 409 while it still "
                    + "owns courses or students.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Center deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No center exists with the supplied identifier.",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "The center still has dependent courses or students.",
                    content = @Content)})
    @DeleteMapping("/{centerId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the center to delete.", example = "1", required = true)
            @PathVariable Long centerId) {
        deleteCenterUseCase.execute(new CenterId(centerId));
        return ResponseEntity.noContent().build();
    }
}