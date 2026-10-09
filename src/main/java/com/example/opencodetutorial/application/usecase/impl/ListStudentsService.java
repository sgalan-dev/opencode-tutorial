package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListStudentsUseCase;
import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default {@link ListStudentsUseCase} implementation.
 */
@Service
public final class ListStudentsService implements ListStudentsUseCase {

    private final StudentRepository studentRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param studentRepository the student repository, must not be null
     */
    public ListStudentsService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> execute() {
        return studentRepository.findAll();
    }
}
