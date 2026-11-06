package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.ProfessorId;

/**
 * Thrown when a professor that does not belong to the center of the group is offered
 * as its tutor. The center of a group is the center that owns the group's course, so
 * a tutor must be a professor of that very center. The REST layer maps this exception
 * to HTTP 409.
 */
public final class TutorInDifferentCenterException extends RuntimeException {

    private final ProfessorId professorId;
    private final CenterId expectedCenterId;

    /**
     * Builds a "tutor belongs to another center" exception.
     *
     * @param professorId      the professor that was offered as tutor, must not be null
     * @param expectedCenterId the center the operation required, must not be null
     */
    public TutorInDifferentCenterException(ProfessorId professorId, CenterId expectedCenterId) {
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
