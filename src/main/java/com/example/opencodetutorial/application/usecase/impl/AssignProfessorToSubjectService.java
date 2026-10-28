package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.AssignProfessorToSubjectUseCase;
import com.example.opencodetutorial.application.usecase.command.AssignProfessorCommand;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.model.Subject;
import com.example.opencodetutorial.domain.model.SubjectId;
import com.example.opencodetutorial.domain.port.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link AssignProfessorToSubjectUseCase} implementation.
 *
 * <p>Resolves the full {@code center -> course -> subject} chain and the professor
 * scoped to the center of the URL, then delegates to the aggregate's
 * {@code assignProfessor(Professor, Course)}, which owns the business rule: the
 * professor who teaches a subject is a professor of the center that owns the subject's
 * course. The rule is mirrored by the foreign key of the {@code subject_teaching}
 * table.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class AssignProfessorToSubjectService implements AssignProfessorToSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param subjectRepository the subject repository, must not be null
     * @param hierarchy         used to validate the hierarchy and load the aggregates
     */
    public AssignProfessorToSubjectService(SubjectRepository subjectRepository, HierarchyQueries hierarchy) {
        this.subjectRepository = subjectRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(AssignProfessorCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        SubjectId subjectId = new SubjectId(command.subjectId());
        ProfessorId professorId = new ProfessorId(command.professorId());

        Course course = hierarchy.requireCourse(centerId, courseId);
        Subject subject = hierarchy.requireSubject(centerId, courseId, subjectId);
        Professor professor = hierarchy.requireProfessor(centerId, professorId);

        subject.assignProfessor(professor, course);
        subjectRepository.save(subject);
    }
}
