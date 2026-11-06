package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the "get group by id" use case.
 */
public interface GetGroupUseCase {

    /**
     * Looks up a group by the course that owns it and its own identifier.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @param groupId  the group identifier, must not be null
     * @return the requested group
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with that identifier exists under that center
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group with that identifier exists under that course
     */
    Group execute(CenterId centerId, CourseId courseId, GroupId groupId);
}