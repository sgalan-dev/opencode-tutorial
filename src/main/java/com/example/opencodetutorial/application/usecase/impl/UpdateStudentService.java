package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateStudentUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateStudentCommand;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.PersonRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default {@link UpdateStudentUseCase} implementation. Replaces the person association
 * and the full set of enrolments in one transaction, and optionally transfers the
 * student to another center (the enrolments supplied in the same payload must then
 * belong to the new center).
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UpdateStudentService implements UpdateStudentUseCase {

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
    public UpdateStudentService(StudentRepository studentRepository,
                                PersonRepository personRepository,
                                HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public Student execute(UpdateStudentCommand command) {
        CenterId urlCenterId = new CenterId(command.centerId());
        StudentId studentId = new StudentId(command.id());
        Student existing = hierarchy.requireStudent(urlCenterId, studentId);

        PersonId personId = new PersonId(command.personId());
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(personId);
        }

        CenterId targetCenterId = command.newCenterId() == null
                ? urlCenterId
                : new CenterId(command.newCenterId());
        if (!targetCenterId.equals(urlCenterId)) {
            hierarchy.requireCenter(targetCenterId);
        }

        Map<GroupId, CourseId> coursesByGroup = new LinkedHashMap<>();
        for (GroupId groupId : toGroupIds(command.groupIds())) {
            Group group = hierarchy.requireGroupOfCenter(targetCenterId, groupId);
            Course course = hierarchy.requireCourseOf(group);
            coursesByGroup.put(groupId, course.id());
        }

        if (!targetCenterId.equals(existing.centerId())) {
            // The enrolments held today belong to the old center; the payload carries
            // the complete new set, resolved above against the target center.
            existing.replaceGroups(Set.of());
            existing.changeCenter(targetCenterId);
        }
        existing.changePerson(personId);
        existing.replaceGroups(coursesByGroup.keySet(), coursesByGroup);
        return studentRepository.save(existing);
    }

    private Set<GroupId> toGroupIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return ids.stream()
                .map(GroupId::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}