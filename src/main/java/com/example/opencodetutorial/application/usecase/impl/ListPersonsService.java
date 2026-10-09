package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.ListPersonsUseCase;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default {@link ListPersonsUseCase} implementation.
 */
@Service
public final class ListPersonsService implements ListPersonsUseCase {

    private final PersonRepository personRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param personRepository the person repository, must not be null
     */
    public ListPersonsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> execute() {
        return personRepository.findAll();
    }
}
