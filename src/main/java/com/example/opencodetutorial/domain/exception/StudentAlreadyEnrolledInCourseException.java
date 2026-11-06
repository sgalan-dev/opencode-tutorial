package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.StudentId;

/**
 * Thrown when enrolling a {@link com.example.opencodetutorial.domain.model.Student}
 * in a group would break the "at most one group per course" rule: the student is
 * already enrolled in another group of the same course. The REST layer maps this
 * exception to HTTP 409, the same status the database unique constraint produces.
 */
public final class StudentAlreadyEnrolledInCourseException extends RuntimeException {

    private final StudentId studentId;
    private final CourseId courseId;

    /**
     * Builds an "already enrolled in this course" exception.
     *
     * @param studentId the student that is already enrolled, may be {@code null} when the
     *                  rule is broken while the student is still being created
     * @param courseId  the course whose rule would be broken; may be {@code null} when the
     *                  conflict is only known to the database
     */
    public StudentAlreadyEnrolledInCourseException(StudentId studentId, CourseId courseId) {
        super(describe(studentId) + " is already enrolled in a group of "
                + (courseId == null ? "the same course" : "course " + courseId.value()));
        this.studentId = studentId;
        this.courseId = courseId;
    }

    private static String describe(StudentId studentId) {
        return studentId == null ? "The student being created" : "Student with id " + studentId.value();
    }

    public StudentId studentId() {
        return studentId;
    }

    public CourseId courseId() {
        return courseId;
    }
}