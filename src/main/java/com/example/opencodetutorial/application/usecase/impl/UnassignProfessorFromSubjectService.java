package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UnassignProfessorFromSubjectUseCase;
import com.example.opencodetutorial.application.usecase.command.UnassignProfessorCommand;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link UnassignProfessorFromSubjectUseCase} implementation. The aggregate's
 * {@code unassignProfessor} is idempotent: if the subject had no professor, the
 * operation is a no-op and no exception is raised.
 *
 * <p>The removal targets one specific professor: the professor named in the URL is the
 * one that has to go. Removing a professor that is not the current teacher leaves the
 * subject untouched, which is the idempotency the spec asks for.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UnassignProfessorFromSubjectService implements UnassignProfessorFromSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param subjectRepository the subject repository, must not be null
     * @param hierarchy         used to validate the hierarchy and load the aggregates
     */
    public UnassignProfessorFromSubjectService(SubjectRepository subjectRepository, HierarchyQueries hierarchy) {
        this.subjectRepository = subjectRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(UnassignProfessorCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        SubjectId subjectId = new SubjectId(command.subjectId());
        ProfessorId professorId = new ProfessorId(command.professorId());

        Subject subject = hierarchy.requireSubject(centerId, courseId, subjectId);
        hierarchy.requireProfessor(centerId, professorId);

        if (professorId.equals(subject.professorId())) {
            subject.unassignProfessor();
            subjectRepository.save(subject);
        }
    }
}
