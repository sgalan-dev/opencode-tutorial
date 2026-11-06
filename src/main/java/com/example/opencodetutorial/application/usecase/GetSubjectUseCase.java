package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;

/**
 * Inbound port for the "get subject by id" use case.
 */
public interface GetSubjectUseCase {

    /**
     * Looks up a subject by the course that owns it and its own identifier.
     *
     * @param centerId  the center identifier, must not be null
     * @param courseId  the course identifier, must not be null
     * @param subjectId the subject identifier, must not be null
     * @return the requested subject
     * @throws com.example.opencodetutorial.domain.exception.CenterNotFoundException
     *         if no center exists with the supplied identifier
     * @throws com.example.opencodetutorial.domain.exception.CourseNotFoundException
     *         if no course with that identifier exists under that center
     * @throws com.example.opencodetutorial.domain.exception.SubjectNotFoundException
     *         if no subject with that identifier exists under that course
     */
    Subject execute(CenterId centerId, CourseId courseId, SubjectId subjectId);
}
