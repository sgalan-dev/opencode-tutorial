package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Thrown when a professor that does not belong to the center of the subject is offered
 * as the teacher of that subject. The center of a subject is the center that owns the
 * subject's course, so the professor who teaches it must be a professor of that very
 * center. The REST layer maps this exception to HTTP 409.
 */
public final class SubjectProfessorInDifferentCenterException extends RuntimeException {

    private final ProfessorId professorId;
    private final CenterId expectedCenterId;

    /**
     * Builds a "professor belongs to another center" exception.
     *
     * @param professorId      the professor that was offered as teacher, must not be null
     * @param expectedCenterId the center the operation required, must not be null
     */
    public SubjectProfessorInDifferentCenterException(ProfessorId professorId, CenterId expectedCenterId) {
        super("Professor with id " + professorId.value() + " belongs to a different center than "
                + expectedCenterId.value());
        this.professorId = professorId;
        this.expectedCenterId = expectedCenterId;
    }

    public ProfessorId professorId() {
        return professorId;
    }

    public CenterId expectedCenterId() {
        return expectedCenterId;
    }
}
