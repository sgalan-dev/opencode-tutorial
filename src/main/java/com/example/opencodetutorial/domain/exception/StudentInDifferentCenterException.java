package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Thrown when a student is enrolled in — or moved to — a center other than the one it
 * belongs to. The REST layer maps this exception to HTTP 409.
 */
public final class StudentInDifferentCenterException extends RuntimeException {

    private final StudentId studentId;
    private final CenterId expectedCenterId;

    /**
     * Builds a "student belongs to another center" exception.
     *
     * @param studentId        the student involved, may be {@code null} while the student
     *                         is still being created
     * @param expectedCenterId the center the operation required, must not be null
     */
    public StudentInDifferentCenterException(StudentId studentId, CenterId expectedCenterId) {
        super(describe(studentId) + " belongs to a different center than "
                + expectedCenterId.value());
        this.studentId = studentId;
        this.expectedCenterId = expectedCenterId;
    }

    private static String describe(StudentId studentId) {
        return studentId == null ? "The student being created" : "Student with id " + studentId.value();
    }

    public StudentId studentId() {
        return studentId;
    }

    public CenterId expectedCenterId() {
        return expectedCenterId;
    }
}