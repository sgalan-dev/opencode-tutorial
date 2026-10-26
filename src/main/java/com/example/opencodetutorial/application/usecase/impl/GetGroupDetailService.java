package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetGroupDetailUseCase;
import com.example.opencodetutorial.application.usecase.view.GroupDetailView;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.exception.ProfessorNotFoundException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.port.PersonRepository;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link GetGroupDetailUseCase} implementation. Orchestrates the group, its
 * course, the personal data of every enrolled student and, when the group has one, its
 * tutor with the personal data of the person behind that tutor. An empty group yields
 * an empty list, never a null.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetGroupDetailService implements GetGroupDetailUseCase {

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    private final ProfessorRepository professorRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository   the student repository, must not be null
     * @param personRepository    the person repository, must not be null
     * @param professorRepository the professor repository, used to resolve the tutor
     * @param hierarchy           used to validate the hierarchy and load the group
     */
    public GetGroupDetailService(StudentRepository studentRepository,
                                 PersonRepository personRepository,
                                 ProfessorRepository professorRepository,
                                 HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
        this.professorRepository = professorRepository;
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

        return new GroupDetailView(group, course, enrolled, toTutorView(group));
    }

    private GroupDetailView.EnrolledStudentView toEnrolledView(Student student) {
        Person person = personRepository.findById(student.personId())
                .orElseThrow(() -> new PersonNotFoundException(student.personId()));
        return new GroupDetailView.EnrolledStudentView(student, person);
    }

    private GroupDetailView.TutorView toTutorView(Group group) {
        if (group.tutorProfessorId() == null) {
            return null;
        }
        Professor professor = professorRepository.findById(group.tutorProfessorId())
                .orElseThrow(() -> new ProfessorNotFoundException(group.tutorProfessorId()));
        Person person = personRepository.findById(professor.personId())
                .orElseThrow(() -> new PersonNotFoundException(professor.personId()));
        return new GroupDetailView.TutorView(professor, person);
    }
}
