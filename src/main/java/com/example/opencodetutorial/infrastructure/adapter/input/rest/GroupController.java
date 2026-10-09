package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreateGroupUseCase;
import com.example.opencodetutorial.application.usecase.DeleteGroupUseCase;
import com.example.opencodetutorial.application.usecase.GetGroupUseCase;
import com.example.opencodetutorial.application.usecase.ListGroupsUseCase;
import com.example.opencodetutorial.application.usecase.UpdateGroupUseCase;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.GroupResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.GroupRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Group}.
 */
@RestController
@RequestMapping("/api/v1/groups")
@Tag(name = "Groups", description = "Operations over the Group aggregate. Persisted to the `student_group` table.")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;
    private final ListGroupsUseCase listGroupsUseCase;
    private final GetGroupUseCase getGroupUseCase;
    private final UpdateGroupUseCase updateGroupUseCase;
    private final DeleteGroupUseCase deleteGroupUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public GroupController(CreateGroupUseCase createGroupUseCase,
                           ListGroupsUseCase listGroupsUseCase,
                           GetGroupUseCase getGroupUseCase,
                           UpdateGroupUseCase updateGroupUseCase,
                           DeleteGroupUseCase deleteGroupUseCase) {
        this.createGroupUseCase = createGroupUseCase;
        this.listGroupsUseCase = listGroupsUseCase;
        this.getGroupUseCase = getGroupUseCase;
        this.updateGroupUseCase = updateGroupUseCase;
        this.deleteGroupUseCase = deleteGroupUseCase;
    }

    /**
     * Lists every group.
     */
    @Operation(
            summary = "List all groups",
            description = "Returns every persisted group, in no particular order.")
    @ApiResponse(responseCode = "200", description = "List of groups (possibly empty).",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GroupResponse.class)))
    @GetMapping
    public ResponseEntity<List<GroupResponse>> list() {
        List<GroupResponse> body = listGroupsUseCase.execute().stream()
                .map(GroupResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a group by its identifier.
     */
    @Operation(
            summary = "Get a group by id",
            description = "Returns the group with the given identifier, or 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "404", description = "No group exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> get(
            @Parameter(description = "Identifier of the group to retrieve.", example = "1", required = true)
            @PathVariable Long id) {
        Group group = getGroupUseCase.execute(new GroupId(id));
        return ResponseEntity.ok(GroupResponse.from(group));
    }

    /**
     * Creates a new group.
     */
    @Operation(
            summary = "Create a new group",
            description = "Persists a new group and returns its identifier in the `Location` header.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Group created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created group.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<GroupResponse> create(
            @RequestBody(description = "Group to create. `name` is required and non-blank.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GroupRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody GroupRequest request) {
        GroupId createdId = createGroupUseCase.execute(GroupRequestMapper.toCreateCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new GroupResponse(createdId.value(), request.name()));
    }

    /**
     * Replaces the group with the given identifier.
     */
    @Operation(
            summary = "Replace a group",
            description = "Replaces the group with the given identifier with the values from the request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Group updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No group exists with the supplied identifier.",
                    content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> update(
            @Parameter(description = "Identifier of the group to replace.", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Replacement payload for the group.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = GroupRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody GroupRequest request) {
        Group updated = updateGroupUseCase.execute(GroupRequestMapper.toUpdateCommand(id, request));
        return ResponseEntity.ok(GroupResponse.from(updated));
    }

    /**
     * Removes the group with the given identifier.
     */
    @Operation(
            summary = "Delete a group",
            description = "Removes the group with the given identifier. Returns 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Group deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No group exists with the supplied identifier.",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the group to delete.", example = "1", required = true)
            @PathVariable Long id) {
        deleteGroupUseCase.execute(new GroupId(id));
        return ResponseEntity.noContent().build();
    }
}
