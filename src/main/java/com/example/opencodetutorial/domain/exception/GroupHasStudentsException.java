package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Thrown when moving a {@link com.example.opencodetutorial.domain.model.Group} to
 * another course while it still has enrolled students. Moving is refused because the
 * denormalized course of every enrolment link points at the old course. The REST
 * layer maps this exception to HTTP 409.
 */
public final class GroupHasStudentsException extends RuntimeException {

    private final GroupId groupId;
    private final CourseId courseId;

    /**
     * Builds a "group still has students" exception.
     *
     * @param groupId  the group that cannot be moved, must not be null
     * @param courseId the course the group was requested to move to, must not be null
     */
    public GroupHasStudentsException(GroupId groupId, CourseId courseId) {
        super("Group with id " + groupId.value() + " still has enrolled students and cannot be moved to course "
                + courseId.value());
        this.groupId = groupId;
        this.courseId = courseId;
    }

    public GroupId groupId() {
        return groupId;
    }

    public CourseId courseId() {
        return courseId;
    }
}