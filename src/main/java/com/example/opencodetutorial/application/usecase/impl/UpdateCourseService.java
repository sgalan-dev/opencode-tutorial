package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateCourseUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateCourseCommand;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.CourseName;
import com.example.opencodetutorial.domain.port.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UpdateCourseUseCase} implementation. Verifies the course belongs to
 * the supplied center, checks the new name is free inside it and asks the aggregate to
 * rename itself.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UpdateCourseService implements UpdateCourseUseCase {

    private final CourseRepository courseRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param courseRepository the course repository, must not be null
     * @param hierarchy        used to validate the center/course chain
     */
    public UpdateCourseService(CourseRepository courseRepository, HierarchyQueries hierarchy) {
        this.courseRepository = courseRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public Course execute(UpdateCourseCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.id());
        Course existing = hierarchy.requireCourse(centerId, courseId);

        CourseName newName = new CourseName(command.name());
        boolean takenByOther = courseRepository.findByCenterId(centerId).stream()
                .anyMatch(course -> course.name().equals(newName) && !course.id().equals(courseId));
        if (takenByOther) {
            throw new DuplicateNameException("course", newName.value(), "in center " + centerId.value());
        }

        existing.rename(newName);
        return courseRepository.save(existing);
    }
}