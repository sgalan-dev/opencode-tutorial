package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetCourseUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetCourseUseCase} implementation. A course that belongs to another
 * center is reported as not found, because it is not reachable under this URL.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetCourseService implements GetCourseUseCase {

    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborator.
     *
     * @param hierarchy used to resolve and validate the center/course chain
     */
    public GetCourseService(HierarchyQueries hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public Course execute(CenterId centerId, CourseId courseId) {
        return hierarchy.requireCourse(centerId, courseId);
    }
}