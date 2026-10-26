package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteStudentUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DeleteStudentUseCase} implementation. Verifies the student belongs to
 * the center in the URL; the enrolment links are removed by the database cascade, since
 * the junction is owned by the student row being deleted.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DeleteStudentService implements DeleteStudentUseCase {

    private final StudentRepository studentRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param studentRepository the student repository, must not be null
     * @param hierarchy         used to validate the center and load the student
     */
    public DeleteStudentService(StudentRepository studentRepository, HierarchyQueries hierarchy) {
        this.studentRepository = studentRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(CenterId centerId, StudentId studentId) {
        hierarchy.requireStudent(centerId, studentId);
        studentRepository.deleteById(studentId);
    }
}