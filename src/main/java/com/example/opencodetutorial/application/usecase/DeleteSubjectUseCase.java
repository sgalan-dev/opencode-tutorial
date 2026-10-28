package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.SubjectId;

/**
 * Inbound port for the "delete subject" use case.
 */
public interface DeleteSubjectUseCase {

    /**
     * Removes the subject with the given identifiers together with the teaching
     * assignment that targeted it.
     *
     * @param centerId  the center identifier, must not be null
     * @param courseId  the course identifier, must not be null
     * @param subjectId the subject identifier, must not be null
     * @throws com.example.opencodetutorial.domain.exception.SubjectNotFoundException
     *         if no subject with that identifier exists under the supplied course
     */
    void execute(CenterId centerId, CourseId courseId, SubjectId subjectId);
}
