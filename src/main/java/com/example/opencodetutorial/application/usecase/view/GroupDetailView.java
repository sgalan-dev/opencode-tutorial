package com.example.opencodetutorial.application.usecase.view;

import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.Student;

import java.util.List;

/**
 * Assembled read model for {@code GET .../groups/{groupId}/students/detail}: the group,
 * the course it belongs to, every enrolled student with the personal data of the
 * associated {@link Person} and, when the group has one, its tutor with the personal
 * data of the person behind that tutor.
 *
 * <p>Lives in the application layer (not in the REST dto package) so the use cases that
 * assemble it do not have to depend on the infrastructure.
 *
 * @param group    the requested group, never null
 * @param course   the course that owns the group, never null
 * @param students the enrolled students with their person data, never null
 * @param tutor    the tutor of the group, or {@code null} when the group has none
 */
public record GroupDetailView(Group group,
                               Course course,
                               List<EnrolledStudentView> students,
                               TutorView tutor) {

    /**
     * One enrolled student together with the personal data of its associated person.
     *
     * @param student the enrolled student, never null
     * @param person  the person the student is associated with, never null
     */
    public record EnrolledStudentView(Student student, Person person) {
    }

    /**
     * The tutor of the group together with the personal data of the person behind it.
     *
     * @param professor the tutor, never null
     * @param person    the person the tutor is associated with, never null
     */
    public record TutorView(Professor professor, Person person) {
    }
}