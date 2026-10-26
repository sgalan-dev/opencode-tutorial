package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;

import java.util.List;

/**
 * Inbound port for the "list students enrolled in a group" use case.
 */
public interface ListStudentsInGroupUseCase {

    /**
     * Returns every student currently enrolled in the supplied group.
     *
     * @param centerId the center identifier, must not be null
     * @param courseId the course identifier, must not be null
     * @param groupId  the group identifier, must not be null
     * @return the enrolled students, never null (empty for an empty group)
     */
    List<Student> execute(CenterId centerId, CourseId courseId, GroupId groupId);
}