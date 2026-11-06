package com.example.opencodetutorial.application.usecase.view;

import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Student;

import java.util.List;

/**
 * Assembled read model for {@code GET .../students/{studentId}/detail}: the student,
 * the center it belongs to, the personal data of the associated {@link Person} and every
 * enrolment, each enriched with the group and the course that group belongs to.
 *
 * <p>Lives in the application layer (not in the REST dto package) so the use cases that
 * assemble it do not have to depend on the infrastructure.
 *
 * @param student    the requested student, never null
 * @param center     the center the student belongs to, never null
 * @param person     the person the student is associated with, never null
 * @param enrolments every enrolment with its group and course, never null
 */
public record StudentDetailView(Student student,
                                Center center,
                                Person person,
                                List<EnrolmentView> enrolments) {

    /**
     * One enrolment of the student: the group plus the course that owns it.
     *
     * @param group  the enrolled group, never null
     * @param course the course that owns the group, never null
     */
    public record EnrolmentView(Group group, Course course) {
    }
}