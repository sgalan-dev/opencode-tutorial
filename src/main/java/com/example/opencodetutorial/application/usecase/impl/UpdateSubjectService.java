package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdateSubjectUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdateSubjectCommand;
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
 * Default {@link UpdateSubjectUseCase} implementation. Verifies the subject belongs to
 * the course in the URL, keeps names unique inside that course and asks the aggregate
 * to rename itself. The subject stays in its course and keeps its professor: both are
 * changed by their own operations.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class UpdateSubjectService implements UpdateSubjectUseCase {

    private final SubjectRepository subjectRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param subjectRepository the subject repository, must not be null
     * @param hierarchy         used to validate the center/course/subject chain
     */
    public UpdateSubjectService(SubjectRepository subjectRepository, HierarchyQueries hierarchy) {
        this.subjectRepository = subjectRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public Subject execute(UpdateSubjectCommand command) {
        CenterId centerId = new CenterId(command.centerId());
        CourseId courseId = new CourseId(command.courseId());
        SubjectId subjectId = new SubjectId(command.id());
        Subject existing = hierarchy.requireSubject(centerId, courseId, subjectId);

        SubjectName newName = new SubjectName(command.name());
        boolean takenByOther = subjectRepository.findByCourseId(courseId).stream()
                .anyMatch(subject -> subject.name().equals(newName) && !subject.id().equals(subjectId));
        if (takenByOther) {
            throw new DuplicateNameException("subject", newName.value(), "in course " + courseId.value());
        }

        existing.rename(newName);
        return subjectRepository.save(existing);
    }
}
