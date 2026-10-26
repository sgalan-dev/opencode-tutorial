package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.application.usecase.view.ProfessorDetailView;
import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Inbound port for the assembled "professor detail" read model.
 */
public interface GetProfessorDetailUseCase {

    /**
     * Assembles the professor, the center it belongs to and the personal data of the
     * person behind it.
     *
     * @param centerId    the center identifier, must not be null
     * @param professorId the professor identifier, must not be null
     * @return the assembled detail
     * @throws com.example.opencodetutorial.domain.exception.ProfessorNotFoundException
     *         if no professor exists with the supplied identifier under that center
     */
    ProfessorDetailView execute(CenterId centerId, ProfessorId professorId);
}
