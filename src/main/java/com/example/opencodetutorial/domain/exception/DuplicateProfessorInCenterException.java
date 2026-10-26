package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.PersonId;

/**
 * Thrown when a person would end up registered twice as a professor of the same
 * center. The same person may be a professor of several centers, but only once per
 * center. The REST layer maps this exception to HTTP 409, the same status the
 * {@code UNIQUE (person_id, center_id)} constraint produces.
 */
public final class DuplicateProfessorInCenterException extends RuntimeException {

    private final PersonId personId;
    private final CenterId centerId;

    /**
     * Builds a "duplicate professor in center" exception.
     *
     * @param personId the person that is already a professor of the center, must not be null
     * @param centerId the center where the duplication was detected, must not be null
     */
    public DuplicateProfessorInCenterException(PersonId personId, CenterId centerId) {
        super("Person with id " + personId.value() + " is already a professor of center with id "
                + centerId.value());
        this.personId = personId;
        this.centerId = centerId;
    }

    public PersonId personId() {
        return personId;
    }

    public CenterId centerId() {
        return centerId;
    }
}
