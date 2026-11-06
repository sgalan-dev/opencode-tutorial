package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Inbound port for the assembled "group detail" read model.
 */
public interface GetGroupDetailUseCase {

    /**
     * Assembles the group, its course and its enrolled students with their personal data.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @param groupId  the group identifier, must not be null
     * @return the assembled detail, with an empty student list for an empty group
     * @throws com.example.opencodetutorial.domain.exception.GroupNotFoundException
     *         if no group with that identifier exists under the supplied course
     */
    GroupDetailView execute(CenterId centerId, CourseId courseId, GroupId groupId);
}