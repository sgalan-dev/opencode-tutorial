package com.example.opencodetutorial.application.usecase.impl;

import com.example.opencodetutorial.application.usecase.UpdatePersonUseCase;
import com.example.opencodetutorial.application.usecase.command.UpdatePersonCommand;
import com.example.opencodetutorial.domain.exception.PersonNotFoundException;
import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.PersonName;
import com.example.opencodetutorial.domain.model.PersonSurname;
import com.example.opencodetutorial.domain.model.SecondSurname;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Service;

/**
 * Default {@link UpdatePersonUseCase} implementation. Loads the existing
 * aggregate, asks it to apply the new state through its behaviour methods,
 * and hands it back to the repository.
 */
@Service
public final class UpdatePersonService implements UpdatePersonUseCase {

    private final PersonRepository personRepository;

    /**
     * Builds the service with its required collaborator.
     *
     * @param personRepository the person repository, must not be null
     */
    public UpdatePersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person execute(UpdatePersonCommand command) {
        PersonId id = new PersonId(command.id());
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));

        PersonName newName = new PersonName(command.name());
        PersonSurname newSurname = new PersonSurname(command.surname());
        SecondSurname newSurname2 = new SecondSurname(command.surname2());

        existing.rename(newName);
        existing.changeSurname(newSurname);
        existing.changeSecondSurname(newSurname2);
        return personRepository.save(existing);
    }
}
