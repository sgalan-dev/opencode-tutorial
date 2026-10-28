package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetSubjectUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetSubjectUseCase} implementation. A subject that belongs to another
 * course is reported as not found, because it is not reachable under this URL.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetSubjectService implements GetSubjectUseCase {

    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborator.
     *
     * @param hierarchy used to resolve and validate the center/course/subject chain
     */
    public GetSubjectService(HierarchyQueries hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public Subject execute(CenterId centerId, CourseId courseId, SubjectId subjectId) {
        return hierarchy.requireSubject(centerId, courseId, subjectId);
    }
}
