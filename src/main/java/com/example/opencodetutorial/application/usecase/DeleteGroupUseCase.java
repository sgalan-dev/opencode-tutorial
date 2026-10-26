package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the "delete group" use case.
 */
public interface DeleteGroupUseCase {

    /**
     * Removes the group with the given identifiers together with every enrolment that
     * targeted it.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @param groupId  the group identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group with that identifier exists under the supplied course
     */
    void execute(CenterId centerId, CourseId courseId, GroupId groupId);
}