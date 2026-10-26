package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteCenterUseCase;
import com.example.opencodetutorial.domain.exception.CenterNotFoundException;
import com.example.opencodetutorial.domain.exception.ResourceHasDependentsException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.port.CenterRepository;
import com.example.opencodetutorial.domain.port.CourseRepository;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import com.example.opencodetutorial.domain.port.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DeleteCenterUseCase} implementation. Verifies existence first so the
 * caller receives a meaningful 404 instead of a silent no-op, then refuses with a
 * conflict while the center still owns courses, students or professors, leaving no
 * partial state.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DeleteCenterService implements DeleteCenterUseCase {

    private final CenterRepository centerRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    /**
     * Builds the service with its required collaborators.
     *
     * @param centerRepository   the center repository, must not be null
     * @param courseRepository   the course repository, used to detect dependents
     * @param studentRepository  the student repository, used to detect dependents
     * @param professorRepository the professor repository, used to detect dependents
     */
    public DeleteCenterService(CenterRepository centerRepository,
                               CourseRepository courseRepository,
                               StudentRepository studentRepository,
                               ProfessorRepository professorRepository) {
        this.centerRepository = centerRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
    }

    @Override
    @Transactional
    public void execute(CenterId id) {
        if (!centerRepository.existsById(id)) {
            throw new CenterNotFoundException(id);
        }
        int courses = courseRepository.findByCenterId(id).size();
        if (courses > 0) {
            throw new ResourceHasDependentsException("Center", id.value(), courses + " course(s)");
        }
        int students = studentRepository.findByCenterId(id).size();
        if (students > 0) {
            throw new ResourceHasDependentsException("Center", id.value(), students + " student(s)");
        }
        int professors = professorRepository.findByCenterId(id).size();
        if (professors > 0) {
            throw new ResourceHasDependentsException("Center", id.value(), professors + " professor(s)");
        }
        centerRepository.deleteById(id);
    }
}