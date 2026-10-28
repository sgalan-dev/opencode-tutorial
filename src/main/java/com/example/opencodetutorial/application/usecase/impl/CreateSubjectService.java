package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreateSubjectUseCase;
import com.example.opencodetutorial.application.usecase.command.CreateSubjectCommand;
import com.example.opencodetutorial.domain.exception.DuplicateNameException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.domain.model.SubjectName;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CreateSubjectUseCase} implementation. Verifies the whole
 * {@code center -> course} chain from the URL, keeps subject names unique inside their
 * course and hands the new aggregate to the repository. A new subject has no professor:
 * the assignment is a later, explicit operation.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class CreateSubjectService implements CreateSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param subjectRepository the subject repository, must not be null
     * @param hierarchy         used to validate the center/course chain
     */
    public CreateSubjectService(SubjectRepository subjectRepository, HierarchyQueries hierarchy) {
        this.subjectRepository = subjectRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public SubjectId execute(CreateSubjectCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        hierarchy.requireCourse(centerId, courseId);

        SubjectName name = new SubjectName(command.name());
        boolean taken = subjectRepository.findByCourseId(courseId).stream()
                .anyMatch(subject -> subject.name().equals(name));
        if (taken) {
            throw new DuplicateNameException("subject", name.value(), "in course " + courseId.value());
        }
        Subject subject = Subject.create(name, courseId);
        return subjectRepository.save(subject).id();
    }
}
