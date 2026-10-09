package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.DeletePersonUseCase;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link DeletePersonUseCase} implementation. Verifies existence
 * first so the caller receives a meaningful 404 instead of a silent no-op.
 */
@Service
public final class DeletePersonService implements DeletePersonUseCase {

    private final PersonRepository personRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param personRepository the person repository, must not be null
     */
    public DeletePersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void execute(PersonId id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException(id);
        }
        personRepository.deleteById(id);
    }
}
