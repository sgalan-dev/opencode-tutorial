package com.example.opencodetutorial.application.usecase.view;

import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.Professor;

/**
 * Assembled read model for {@code GET .../professors/{professorId}/detail}: the
 * professor, the center it belongs to and the personal data of the associated
 * {@link Person}.
 *
 * <p>The groups tutored by the professor are deliberately NOT part of this view: the
 * relation stays one-directional in the API, even though the application layer can
 * query it for the deletion guard.
 *
 * <p>Lives in the application layer (not in the REST dto package) so the use cases that
 * assemble it do not have to depend on the infrastructure.
 *
 * @param professor the requested professor, never null
 * @param center    the center the professor belongs to, never null
 * @param person    the person the professor is associated with, never null
 */
public record ProfessorDetailView(Professor professor, Center center, Person person) {
}
