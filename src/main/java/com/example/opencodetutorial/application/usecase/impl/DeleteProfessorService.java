package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeleteProfessorUseCase;
import com.example.opencodetutorial.domain.exception.ResourceHasDependentsException;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.ProfessorId;
import com.example.opencodetutorial.domain.port.GroupRepository;
import com.example.opencodetutorial.domain.port.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link DeleteProfessorUseCase} implementation. Verifies the professor belongs
 * to the center in the URL and refuses with a conflict while it still tutors at least
 * one group, leaving no partial state behind.
 *
 * <p>Not final: Spring proxies the class to apply {@code @Transactional}.
 */
@Service
public class DeleteProfessorService implements DeleteProfessorUseCase {

    private final ProfessorRepository professorRepository;
    private final GroupRepository groupRepository;
    private final HierarchyQueries hierarchy;

    /**
     * Builds the service with its required collaborators.
     *
     * @param professorRepository the professor repository, must not be null
     * @param groupRepository     the group repository, used to detect the tutored groups
     * @param hierarchy           used to validate the center and load the professor
     */
    public DeleteProfessorService(ProfessorRepository professorRepository,
                                  GroupRepository groupRepository,
                                  HierarchyQueries hierarchy) {
        this.professorRepository = professorRepository;
        this.groupRepository = groupRepository;
        this.hierarchy = hierarchy;
    }

    @Override
    @Transactional
    public void execute(CenterId centerId, ProfessorId professorId) {
        hierarchy.requireProfessor(centerId, professorId);
        int tutoredGroups = groupRepository.findByTutorProfessorId(professorId).size();
        if (tutoredGroups > 0) {
            throw new ResourceHasDependentsException("Professor", professorId.value(),
                    tutoredGroups + " group(s) as tutor");
        }
        professorRepository.deleteById(professorId);
    }
}
