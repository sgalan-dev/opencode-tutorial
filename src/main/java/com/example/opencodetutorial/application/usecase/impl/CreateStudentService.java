package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateStudentUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateStudentCommand;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.PersonRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default {@link CreateStudentUseCase} implementation.
 *
 * <p>Verifies the parent center and the referenced person exist, resolves every
 * supplied group through its course so the enrolment rules (same center, one group per
 * course) are applied by the aggregate, and then persists the student.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class CreateStudentService implements CreateStudentUseCase {

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param personRepository  the person repository, must not be null
     * @param hierarchy         used to validate the center and resolve the groups
     */
    public CreateStudentService(StudentRepository studentRepository,
                                PersonRepository personRepository,
                                HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public StudentId execute(CreateStudentCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        PersonId personId = new PersonId(command.personId());

        hierarchy.requireCenter(centerId);
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(personId);
        }

        Student student = Student.create(personId, centerId, Set.of());
        for (GroupId groupId : toGroupIds(command.groupIds())) {
            Group group = hierarchy.requireGroupOfCenter(centerId, groupId);
            Course course = hierarchy.requireCourseOf(group);
            student.attachToGroup(group, course);
        }
        return studentRepository.save(student).id();
    }

    private Set<GroupId> toGroupIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        Set<GroupId> groupIds = new LinkedHashSet<>();
        ids.forEach(id -> groupIds.add(new GroupId(id)));
        return groupIds;
    }
}