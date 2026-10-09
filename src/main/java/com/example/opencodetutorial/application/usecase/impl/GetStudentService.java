package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetStudentUseCase;
import com.example.opencodetutorial.domain.exception.StudentNotFoundException;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link GetStudentUseCase} implementation. Surfaces a missing student
 * as a domain exception so the REST layer can map it to HTTP 404.
 */
@Service
public final class GetStudentService implements GetStudentUseCase {

    private final StudentRepository studentRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param studentRepository the student repository, must not be null
     */
    public GetStudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student execute(StudentId id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }
}
