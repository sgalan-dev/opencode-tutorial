package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListCoursesUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.port.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link ListCoursesUseCase} implementation. The parent center must exist, so
 * an unknown center is a 404 instead of an empty list.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class ListCoursesService implements ListCoursesUseCase {

    private final CourseRepository courseRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param courseRepository the course repository, must not be null
     * @param hierarchy        used to verify the parent center exists
     */
    public ListCoursesService(CourseRepository courseRepository, HierarchyQueries hierarchy) {
        this.courseRepository = courseRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> execute(CenterId centerId) {
        hierarchy.requireCenter(centerId);
        return courseRepository.findByCenterId(centerId);
    }
}