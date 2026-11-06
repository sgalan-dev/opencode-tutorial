package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteCourseUseCase;
import com.example.opencodetutorial.domain.exception.ResourceHasDependentsException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.port.CourseRepository;
import com.example.opencodetutorial.domain.port.GroupRepository;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DeleteCourseUseCase} implementation. Refuses with a conflict while the
 * course still owns groups or subjects, so no partial state is left behind.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DeleteCourseService implements DeleteCourseUseCase {

    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param courseRepository  the course repository, must not be null
     * @param groupRepository   used to detect dependent groups
     * @param subjectRepository used to detect dependent subjects
     * @param hierarchy         used to validate the center/course chain
     */
    public DeleteCourseService(CourseRepository courseRepository,
                               GroupRepository groupRepository,
                               SubjectRepository subjectRepository,
                               HierarchyQueries hierarchy) {
        this.courseRepository = courseRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(CenterId centerId, CourseId courseId) {
        Course course = hierarchy.requireCourse(centerId, courseId);
        int groups = groupRepository.findByCourseId(courseId).size();
        if (groups > 0) {
            throw new ResourceHasDependentsException("Course", course.id().value(),
                    groups + " group(s)");
        }
        int subjects = subjectRepository.findByCourseId(courseId).size();
        if (subjects > 0) {
            throw new ResourceHasDependentsException("Course", course.id().value(),
                    subjects + " subject(s)");
        }
        courseRepository.deleteById(courseId);
    }
}