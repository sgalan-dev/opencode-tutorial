package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single source of HTTP error mapping for the REST layer.
 *
 * <ul>
 *   <li>Domain {@code *NotFoundException} → {@code 404 Not Found}.</li>
 *   <li>{@code MethodArgumentNotValidException} (Bean Validation) → {@code 400 Bad Request}
 *       with a per-field breakdown.</li>
 *   <li>{@code IllegalArgumentException} (raised by value-object compact constructors
 *       when a controller forgets to validate) → {@code 400 Bad Request}.</li>
 * </ul>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Builds the response body shared by every error response, with the supplied status.
     *
     * @param status  the HTTP status
     * @param error   a short error label
     * @param message the human-readable message
     * @return a mutable map ready to be enriched with extra fields
     */
    private static Map<String, Object> body(HttpStatus status, String error, String message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", status.value());
        map.put("error", error);
        map.put("message", message);
        return map;
    }

    /**
     * Maps a missing person to HTTP 404.
     */
    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePersonNotFound(PersonNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, "Person not found", ex.getMessage()));
    }

    /**
     * Maps a missing group to HTTP 404.
     */
    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleGroupNotFound(GroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, "Group not found", ex.getMessage()));
    }

    /**
     * Maps a missing student to HTTP 404.
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotFound(StudentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, "Student not found", ex.getMessage()));
    }

    /**
     * Maps Bean Validation failures on {@code @Valid} request bodies to HTTP 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(ApiExceptionHandler::formatFieldError)
                .toList();
        Map<String, Object> body = body(HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields are invalid");
        body.put("fields", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Maps a domain invariant violation (raised by a value-object compact constructor)
     * to HTTP 400. Useful when a request payload bypasses Bean Validation.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(body(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage()));
    }

    private static String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
