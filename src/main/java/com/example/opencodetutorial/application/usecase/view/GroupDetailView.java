package com.example.opencodetutorial.application.usecase.view;

import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Student;

import java.util.List;

/**
 * Assembled read model for {@code GET .../groups/{groupId}/students/detail}: the group,
 * the course it belongs to and every enrolled student with the personal data of the
 * associated {@link Person}.
 *
 * <p>Lives in the application layer (not in the REST dto package) so the use cases that
 * assemble it do not have to depend on the infrastructure.
 *
 * @param group    the requested group, never null
 * @param course   the course that owns the group, never null
 * @param students the enrolled students with their person data, never null
 */
public record GroupDetailView(Group group, Course course, List<EnrolledStudentView> students) {

    /**
     * One enrolled student together with the personal data of its associated person.
     *
     * @param student the enrolled student, never null
     * @param person  the person the student is associated with, never null
     */
    public record EnrolledStudentView(Student student, Person person) {
    }
}