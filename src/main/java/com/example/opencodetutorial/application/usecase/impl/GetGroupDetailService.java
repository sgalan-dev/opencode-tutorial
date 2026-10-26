package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetGroupDetailUseCase;
import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.port.PersonRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link GetGroupDetailUseCase} implementation. Orchestrates the group, its
 * course and the personal data of every enrolled student. An empty group yields an
 * empty list, never a null.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetGroupDetailService implements GetGroupDetailUseCase {

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param personRepository  the person repository, must not be null
     * @param hierarchy         used to validate the hierarchy and load the group
     */
    public GetGroupDetailService(StudentRepository studentRepository,
                                 PersonRepository personRepository,
                                 HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupDetailView execute(CenterId centerId, CourseId courseId, GroupId groupId) {
        Group group = hierarchy.requireGroup(centerId, courseId, groupId);
        Course course = hierarchy.requireCourseOf(group);

        List<GroupDetailView.EnrolledStudentView> enrolled = studentRepository.findAll().stream()
                .filter(student -> student.isAttachedToGroup(groupId))
                .map(this::toEnrolledView)
                .toList();

        return new GroupDetailView(group, course, enrolled);
    }

    private GroupDetailView.EnrolledStudentView toEnrolledView(Student student) {
        Person person = personRepository.findById(student.personId())
                .orElseThrow(() -> new PersonNotFoundException(student.personId()));
        return new GroupDetailView.EnrolledStudentView(student, person);
    }
}