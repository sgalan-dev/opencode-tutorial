package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteStudentUseCase;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link DeleteStudentUseCase} implementation. Verifies existence
 * first so the caller receives a meaningful 404 instead of a silent no-op.
 */
@Service
public final class DeleteStudentService implements DeleteStudentUseCase {

    private final StudentRepository studentRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param studentRepository the student repository, must not be null
     */
    public DeleteStudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void execute(StudentId id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }
}
