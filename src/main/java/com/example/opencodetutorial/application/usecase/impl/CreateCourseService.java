package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateCourseUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateCourseCommand;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.CourseName;
import com.example.opencodetutorial.domain.port.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CreateCourseUseCase} implementation. Verifies the parent center
 * exists, checks the name is still free inside it and hands the new aggregate to the
 * repository.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class CreateCourseService implements CreateCourseUseCase {

    private final CourseRepository courseRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param courseRepository the course repository, must not be null
     * @param hierarchy        used to verify the parent center exists
     */
    public CreateCourseService(CourseRepository courseRepository, HierarchyQueries hierarchy) {
        this.courseRepository = courseRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public CourseId execute(CreateCourseCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        hierarchy.requireCenter(centerId);

        CourseName name = new CourseName(command.name());
        boolean taken = courseRepository.findByCenterId(centerId).stream()
                .anyMatch(course -> course.name().equals(name));
        if (taken) {
            throw new DuplicateNameException("course", name.value(), "in center " + centerId.value());
        }
        Course course = Course.create(name, centerId);
        return courseRepository.save(course).id();
    }
}