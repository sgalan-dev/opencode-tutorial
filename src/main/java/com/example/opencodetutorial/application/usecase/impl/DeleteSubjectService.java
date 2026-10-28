package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteSubjectUseCase;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DeleteSubjectUseCase} implementation. Verifies the subject belongs to
 * the course in the URL and removes the teaching assignment that targeted it before
 * deleting the subject itself, so the junction never keeps rows pointing at a missing
 * subject.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DeleteSubjectService implements DeleteSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param subjectRepository the subject repository, must not be null
     * @param hierarchy         used to validate the center/course/subject chain
     */
    public DeleteSubjectService(SubjectRepository subjectRepository, HierarchyQueries hierarchy) {
        this.subjectRepository = subjectRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(CenterId centerId, CourseId courseId, SubjectId subjectId) {
        hierarchy.requireSubject(centerId, courseId, subjectId);
        subjectRepository.deleteById(subjectId);
    }
}
