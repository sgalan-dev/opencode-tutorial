package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.EnrollStudentInGroupUseCase;
import com.example.opencodetutorial.application.usecase.command.EnrollStudentInGroupCommand;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link EnrollStudentInGroupUseCase} implementation.
 *
 * <p>Loads the group and the student behind the full {@code center -> course -> group}
 * chain, then delegates to the aggregate's {@code attachToGroup(Group, Course)}, which
 * owns the business rules: the enrolment stays inside the student's center and a
 * student never ends up in two groups of the same course. Both rules are mirrored by the
 * database, so a concurrent request that slips past the aggregate is still rejected by
 * the {@code UNIQUE (student_id, course_id)} constraint and surfaces as the same 409.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class EnrollStudentInGroupService implements EnrollStudentInGroupUseCase {

    private final StudentRepository studentRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param hierarchy         used to validate the hierarchy and load the aggregates
     */
    public EnrollStudentInGroupService(StudentRepository studentRepository, HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(EnrollStudentInGroupCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        GroupId groupId = new GroupId(command.groupId());
        StudentId studentId = new StudentId(command.studentId());

        Course course = hierarchy.requireCourse(centerId, courseId);
        Group group = hierarchy.requireGroup(centerId, courseId, groupId);
        Student student = hierarchy.requireStudent(centerId, studentId);

        student.attachToGroup(group, course);
        studentRepository.save(student);
    }
}