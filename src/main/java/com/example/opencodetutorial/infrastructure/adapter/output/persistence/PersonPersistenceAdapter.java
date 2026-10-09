package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;
import com.example.opencodetutorial.domain.model.PersonName;
import com.example.opencodetutorial.domain.model.PersonSurname;
import com.example.opencodetutorial.domain.model.SecondSurname;
import com.example.opencodetutorial.domain.port.PersonRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA-backed implementation of {@link PersonRepository}.
 * Owns the only translation between the domain {@link Person} aggregate and the
 * {@link PersonJpaEntity} row representation.
 */
@Component
public class PersonPersistenceAdapter implements PersonRepository {

    private final PersonJpaRepository repository;

    /**
     * Builds the adapter with its required collaborator.
     *
     * @param repository the JPA repository, must not be null
     */
    public PersonPersistenceAdapter(PersonJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Person save(Person person) {
        PersonJpaEntity entity = toEntity(person);
        PersonJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findById(PersonId id) {
        return repository.findById(id.value()).map(PersonPersistenceAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return repository.findAll().stream()
                .map(PersonPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(PersonId id) {
        repository.deleteById(id.value());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(PersonId id) {
        return repository.existsById(id.value());
    }

    private static PersonJpaEntity toEntity(Person person) {
        PersonJpaEntity entity = new PersonJpaEntity();
        if (person.id() != null) {
            entity.setId(person.id().value());
        }
        entity.setName(person.name().value());
        entity.setSurname(person.surname().value());
        entity.setSurname2(person.surname2() == null ? null : person.surname2().value());
        entity.setCreatedAt(person.createdAt());
        entity.setUpdatedAt(person.updatedAt());
        return entity;
    }

    private static Person toDomain(PersonJpaEntity entity) {
        return Person.rehydrate(
                new PersonId(entity.getId()),
                new PersonName(entity.getName()),
                new PersonSurname(entity.getSurname()),
                new SecondSurname(entity.getSurname2()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
