package com.example.opencodetutorial.infrastructure.adapter.input.rest;

import com.example.opencodetutorial.domain.exception.CenterNotFoundException;
import com.example.opencodetutorial.domain.exception.CourseNotFoundException;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.exception.DuplicateProfessorInCenterException;
import com.example.opencodetutorial.domain.exception.GroupHasStudentsException;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.exception.ProfessorNotFoundException;
import com.example.opencodetutorial.domain.exception.ResourceHasDependentsException;
import com.example.opencodetutorial.domain.exception.StudentAlreadyEnrolledInCourseException;
import com.example.opencodetutorial.domain.exception.StudentInDifferentCenterException;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.exception.TutorInDifferentCenterException;
import org.springframework.dao.DataIntegrityViolationException;
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
 *   <li>Domain conflicts ({@code *AlreadyEnrolled*}, {@code *HasStudents*},
 *       {@code *InDifferentCenter*}, {@code DuplicateName*},
 *       {@code ResourceHasDependents*}) and any database constraint violation → {@code 409 Conflict}.</li>
 *   <li>{@code MethodArgumentNotValidException} (Bean Validation) → {@code 400 Bad Request}
 *       with a per-field breakdown.</li>
 *   <li>{@code IllegalArgumentException} (raised by value-object compact constructors
 *       when a controller forgets to validate) → {@code 400 Bad Request}.</li>
 * </ul>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Name of the unique constraint that backs the "at most one group per course" rule.
     */
    private static final String STUDENT_COURSE_CONSTRAINT = "UK_STUDENT_COURSE";

    /**
     * Name of the unique constraint that backs the "one professor per person per
     * center" rule.
     */
    private static final String PROFESSOR_PERSON_CENTER_CONSTRAINT = "UK_PROFESSOR_PERSON_CENTER";

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
     * Maps a missing center to HTTP 404.
     */
    @ExceptionHandler(CenterNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCenterNotFound(CenterNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, "Center not found", ex.getMessage()));
    }

    /**
     * Maps a missing course to HTTP 404.
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotFound(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, "Course not found", ex.getMessage()));
    }

    /**
     * Maps a missing professor to HTTP 404.
     */
    @ExceptionHandler(ProfessorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProfessorNotFound(ProfessorNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(HttpStatus.NOT_FOUND, "Professor not found", ex.getMessage()));
    }

    /**
     * Maps a duplicate name to HTTP 409.
     */
    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateName(DuplicateNameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Duplicate name", ex.getMessage()));
    }

    /**
     * Maps a deletion refused because of dependents to HTTP 409.
     */
    @ExceptionHandler(ResourceHasDependentsException.class)
    public ResponseEntity<Map<String, Object>> handleHasDependents(ResourceHasDependentsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Resource has dependents", ex.getMessage()));
    }

    /**
     * Maps moving a group that still has students to HTTP 409.
     */
    @ExceptionHandler(GroupHasStudentsException.class)
    public ResponseEntity<Map<String, Object>> handleGroupHasStudents(GroupHasStudentsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Group has students", ex.getMessage()));
    }

    /**
     * Maps breaking the "at most one group per course" rule to HTTP 409.
     */
    @ExceptionHandler(StudentAlreadyEnrolledInCourseException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyEnrolled(
            StudentAlreadyEnrolledInCourseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Student already enrolled in course", ex.getMessage()));
    }

    /**
     * Maps an enrolment that crosses centers to HTTP 409.
     */
    @ExceptionHandler(StudentInDifferentCenterException.class)
    public ResponseEntity<Map<String, Object>> handleDifferentCenter(StudentInDifferentCenterException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Student belongs to another center", ex.getMessage()));
    }

    /**
     * Maps a duplicated professor in a center to HTTP 409.
     */
    @ExceptionHandler(DuplicateProfessorInCenterException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateProfessor(
            DuplicateProfessorInCenterException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Duplicate professor in center", ex.getMessage()));
    }

    /**
     * Maps a tutor that belongs to another center to HTTP 409.
     */
    @ExceptionHandler(TutorInDifferentCenterException.class)
    public ResponseEntity<Map<String, Object>> handleTutorInDifferentCenter(
            TutorInDifferentCenterException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "Tutor belongs to another center", ex.getMessage()));
    }

    /**
     * Maps a database constraint violation to HTTP 409. This is the safety net for the
     * "one group per course per student" rule: a request that slips past the domain
     * check (for instance two concurrent ones) is still rejected by the unique
     * constraint and answered with the very same status. The same applies to the
     * "one professor per person per center" rule.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String label;
        if (mentionsConstraint(ex, STUDENT_COURSE_CONSTRAINT)) {
            label = "Student already enrolled in course";
        } else if (mentionsConstraint(ex, PROFESSOR_PERSON_CENTER_CONSTRAINT)) {
            label = "Duplicate professor in center";
        } else {
            label = "Conflict";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, label, rootMessage(ex)));
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

    /**
     * Tells whether the exception chain mentions the given constraint name.
     *
     * @param throwable the exception to inspect, must not be null
     * @param token     the constraint name in upper case, must not be null
     * @return {@code true} when any message of the chain contains the token
     */
    private static boolean mentionsConstraint(Throwable throwable, String token) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.toUpperCase().contains(token)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * Extracts the most specific message of the exception chain, falling back to the
     * exception type when no message is available.
     *
     * @param throwable the exception to inspect, must not be null
     * @return the message to expose to the caller
     */
    private static String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null ? throwable.getClass().getSimpleName() : message;
    }

    private static String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}