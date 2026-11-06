package com.example.opencodetutorial.infrastructure.adapter.input.rest.mapper;

import com.example.opencodetutorial.application.usecase.command.CreateCourseCommand;
import com.example.opencodetutorial.application.usecase.command.UpdateCourseCommand;
import com.example.opencodetutorial.infrastructure.adapter.input.rest.dto.CourseRequest;

/**
 * Translates {@link CourseRequest} payloads into framework-free application commands.
 * The owning center always comes from the canonical URL, never from the body.
 */
public final class CourseRequestMapper {

    private CourseRequestMapper() {
        // Utility class.
    }

    /**
     * Builds a {@link CreateCourseCommand} from the parent center and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param request  the request payload, must not be null
     * @return the create command
     */
    public static CreateCourseCommand toCreateCommand(Long centerId, CourseRequest request) {
        return new CreateCourseCommand(centerId, request.name());
    }

    /**
     * Builds an {@link UpdateCourseCommand} from the URL identifiers and a request payload.
     *
     * @param centerId the parent center taken from the URL
     * @param courseId the target course taken from the URL
     * @param request  the request payload, must not be null
     * @return the update command
     */
    public static UpdateCourseCommand toUpdateCommand(Long centerId, Long courseId, CourseRequest request) {
        return new UpdateCourseCommand(centerId, courseId, request.name());
    }
}