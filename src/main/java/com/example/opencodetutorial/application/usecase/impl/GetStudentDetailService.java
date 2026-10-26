package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetStudentDetailUseCase;
import com.example.opencodetutorial.application.usecase.view.StudentDetailView;
import com.example.opencodetutorial.domain.exception.CourseNotFoundException;
import com.example.opencodetutorial.domain.exception.GroupNotFoundException;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.CourseRepository;
import com.example.opencodetutorial.domain.port.GroupRepository;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link GetStudentDetailUseCase} implementation. Orchestrates the student,
 * its center, the person behind it and every enrolment with the group and the course
 * that group belongs to. A student without enrolments yields an empty list, never null.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetStudentDetailService implements GetStudentDetailUseCase {

    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final PersonRepository personRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param groupRepository   the group repository, must not be null
     * @param courseRepository  the course repository, must not be null
     * @param personRepository  the person repository, must not be null
     * @param hierarchy         used to validate the center and load the student
     */
    public GetStudentDetailService(GroupRepository groupRepository,
                                   CourseRepository courseRepository,
                                   PersonRepository personRepository,
                                   HierarchyQueries hierarchy) {
        this.groupRepository = groupRepository;
        this.courseRepository = courseRepository;
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDetailView execute(CenterId centerId, StudentId studentId) {
        Student student = hierarchy.requireStudent(centerId, studentId);
        Center center = hierarchy.requireCenter(centerId);
        Person person = personRepository.findById(student.personId())
                .orElseThrow(() -> new PersonNotFoundException(student.personId()));

        List<StudentDetailView.EnrolmentView> enrolments = student.groupIds().stream()
                .map(this::toEnrolmentView)
                .toList();

        return new StudentDetailView(student, center, person, enrolments);
    }

    private StudentDetailView.EnrolmentView toEnrolmentView(GroupId groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
        Course course = courseRepository.findById(group.courseId())
                .orElseThrow(() -> new CourseNotFoundException(group.courseId()));
        return new StudentDetailView.EnrolmentView(group, course);
    }
}