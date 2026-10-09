package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.GetPersonUseCase;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link GetPersonUseCase} implementation. Surfaces a missing person as a
 * domain exception so the REST layer can map it to HTTP 404.
 */
@Service
public final class GetPersonService implements GetPersonUseCase {

    private final PersonRepository personRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param personRepository the person repository, must not be null
     */
    public GetPersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person execute(PersonId id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }
}
