package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetStudentUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link GetStudentUseCase} implementation. A student that belongs to another
 * center is reported as not found, because it is not reachable under this URL.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class GetStudentService implements GetStudentUseCase {

    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborator.
     *
     * @param hierarchy used to validate the center and load the student
     */
    public GetStudentService(HierarchyQueries hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public Student execute(CenterId centerId, StudentId studentId) {
        return hierarchy.requireStudent(centerId, studentId);
    }
}