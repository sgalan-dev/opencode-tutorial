package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.CreatePersonUseCase;
import com.example.opencodetutorial.application.usecase.command.CreatePersonCommand;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.PersonName;
import com.example.opencodetutorial.domain.model.PersonSurname;
import com.example.opencodetutorial.domain.model.SecondSurname;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link CreatePersonUseCase} implementation.
 * Maps the raw command into the domain, asks the aggregate to validate itself
 * (via the {@link PersonName}/{@link PersonSurname} value objects) and hands
 * the result to the repository, which is responsible for assigning an identifier.
 */
@Service
public final class CreatePersonService implements CreatePersonUseCase {

    private final PersonRepository personRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param personRepository the person repository, must not be null
     */
    public CreatePersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonId execute(CreatePersonCommand command) {
        PersonName name = new PersonName(command.name());
        PersonSurname surname = new PersonSurname(command.surname());
        SecondSurname surname2 = new SecondSurname(command.surname2());
        Person person = Person.create(name, surname, surname2);
        return personRepository.save(person).id();
    }
}
