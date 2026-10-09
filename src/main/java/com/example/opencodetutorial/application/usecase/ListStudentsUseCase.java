package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Student;

import java.util.List;

/**
 * Inbound port for the "list students" use case.
 */
public interface ListStudentsUseCase {

    /**
     * Returns every persisted student, in no particular order.
     *
     * @return the list of students, never null
     */
    List<Student> execute();
}
