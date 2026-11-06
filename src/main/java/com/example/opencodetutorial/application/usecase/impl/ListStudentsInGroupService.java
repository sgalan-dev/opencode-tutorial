package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListStudentsInGroupUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.GroupId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link ListStudentsInGroupUseCase} implementation.
 *
 * <p>The enrolments live in the {@link Student} aggregate (the junction table is owned
 * by the student), so the lookup goes through {@link StudentRepository#findAll()} and
 * filters in memory by the requested group. That is the right trade-off at the expected
 * size of this domain; if the number of students grows, push the filter down to
 * {@code StudentJpaRepository} instead of changing this contract.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class ListStudentsInGroupService implements ListStudentsInGroupUseCase {

    private final StudentRepository studentRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param hierarchy         used to validate the hierarchy
     */
    public ListStudentsInGroupService(StudentRepository studentRepository, HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> execute(CenterId centerId, CourseId courseId, GroupId groupId) {
        hierarchy.requireGroup(centerId, courseId, groupId);
        return studentRepository.findAll().stream()
                .filter(student -> student.isAttachedToGroup(groupId))
                .toList();
    }
}