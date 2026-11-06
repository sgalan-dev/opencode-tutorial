package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListStudentsUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link ListStudentsUseCase} implementation. The parent center must exist, so
 * an unknown center is a 404 instead of an empty list.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class ListStudentsService implements ListStudentsUseCase {

    private final StudentRepository studentRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param hierarchy         used to verify the parent center exists
     */
    public ListStudentsService(StudentRepository studentRepository, HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> execute(CenterId centerId) {
        hierarchy.requireCenter(centerId);
        return studentRepository.findByCenterId(centerId);
    }
}