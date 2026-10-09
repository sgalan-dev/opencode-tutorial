package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.application.usecase.CreatePersonUseCase;
import com.example.opencodetutorial.application.usecase.DeletePersonUseCase;
import com.example.opencodetutorial.application.usecase.GetPersonUseCase;
import com.example.opencodetutorial.application.usecase.ListPersonsUseCase;
import com.example.opencodetutorial.application.usecase.UpdatePersonUseCase;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.PersonRequest;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.PersonResponse;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper.PersonRequestMapper;
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
 * REST adapter for {@link com.example.opencodetutorial.domain.model.Person}.
 * Delegates every operation to the corresponding inbound port so the application
 * layer stays unaware of HTTP.
 */
@RestController
@RequestMapping("/api/v1/persons")
@Tag(name = "Persons", description = "Operations over the Person aggregate (administrative data of a person).")
public class PersonController {

    private final CreatePersonUseCase createPersonUseCase;
    private final ListPersonsUseCase listPersonsUseCase;
    private final GetPersonUseCase getPersonUseCase;
    private final UpdatePersonUseCase updatePersonUseCase;
    private final DeletePersonUseCase deletePersonUseCase;

    /**
     * Builds the controller with its required collaborators.
     */
    public PersonController(CreatePersonUseCase createPersonUseCase,
                            ListPersonsUseCase listPersonsUseCase,
                            GetPersonUseCase getPersonUseCase,
                            UpdatePersonUseCase updatePersonUseCase,
                            DeletePersonUseCase deletePersonUseCase) {
        this.createPersonUseCase = createPersonUseCase;
        this.listPersonsUseCase = listPersonsUseCase;
        this.getPersonUseCase = getPersonUseCase;
        this.updatePersonUseCase = updatePersonUseCase;
        this.deletePersonUseCase = deletePersonUseCase;
    }

    /**
     * Lists every person.
     */
    @Operation(
            summary = "List all persons",
            description = "Returns every persisted person, in no particular order.")
    @ApiResponse(responseCode = "200", description = "List of persons (possibly empty).",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PersonResponse.class)))
    @GetMapping
    public ResponseEntity<List<PersonResponse>> list() {
        List<PersonResponse> body = listPersonsUseCase.execute().stream()
                .map(PersonResponse::from)
                .toList();
        return ResponseEntity.ok(body);
    }

    /**
     * Retrieves a person by its identifier.
     */
    @Operation(
            summary = "Get a person by id",
            description = "Returns the person with the given identifier, or 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PersonResponse.class))),
            @ApiResponse(responseCode = "404", description = "No person exists with the supplied identifier.",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> get(
            @Parameter(description = "Identifier of the person to retrieve.", example = "1", required = true)
            @PathVariable Long id) {
        Person person = getPersonUseCase.execute(new PersonId(id));
        return ResponseEntity.ok(PersonResponse.from(person));
    }

    /**
     * Creates a new person.
     */
    @Operation(
            summary = "Create a new person",
            description = "Persists a new person and returns its identifier in the `Location` header.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Person created. The `Location` header points at the new resource.",
                    headers = @Header(name = HttpHeaders.LOCATION, description = "URI of the newly created person.",
                            schema = @Schema(type = "string", format = "uri")),
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PersonResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<PersonResponse> create(
            @RequestBody(description = "Person to create. `name` and `surname` are required and non-blank; `surname2` is optional.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PersonRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody PersonRequest request) {
        PersonId createdId = createPersonUseCase.execute(PersonRequestMapper.toCreateCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId.value())
                .toUri();
        return ResponseEntity.created(location)
                .body(new PersonResponse(createdId.value(), request.name(), request.surname(), request.surname2()));
    }

    /**
     * Replaces the person with the given identifier.
     */
    @Operation(
            summary = "Replace a person",
            description = "Replaces the person with the given identifier with the values from the request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person updated.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PersonResponse.class))),
            @ApiResponse(responseCode = "400", description = "One or more fields are invalid.",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No person exists with the supplied identifier.",
                    content = @Content)})
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(
            @Parameter(description = "Identifier of the person to replace.", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(description = "Replacement payload for the person.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PersonRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody PersonRequest request) {
        Person updated = updatePersonUseCase.execute(PersonRequestMapper.toUpdateCommand(id, request));
        return ResponseEntity.ok(PersonResponse.from(updated));
    }

    /**
     * Removes the person with the given identifier.
     */
    @Operation(
            summary = "Delete a person",
            description = "Removes the person with the given identifier. Returns 404 if it does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Person deleted. No content.", content = @Content),
            @ApiResponse(responseCode = "404", description = "No person exists with the supplied identifier.",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifier of the person to delete.", example = "1", required = true)
            @PathVariable Long id) {
        deletePersonUseCase.execute(new PersonId(id));
        return ResponseEntity.noContent().build();
    }
}
