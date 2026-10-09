package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.Person;
import com.example.opencodetutorial.domain.model.PersonId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Person} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that persons are
 * stored as JPA entities; the domain layer sees only this abstraction.
 */
public interface PersonRepository {

    /**
     * Persists a person, inserting a new aggregate or updating an existing one.
     *
     * @param person the person to save, must not be null
     * @return the saved person (with its identifier assigned if it was new)
     */
    Person save(Person person);

    /**
     * Looks up a person by its identifier.
     *
     * @param id the person identifier, must not be null
     * @return an {@link Optional} containing the person if found, otherwise empty
     */
    Optional<Person> findById(PersonId id);

    /**
     * Lists every persisted person, in no particular order.
     *
     * @return the list of persons, never null
     */
    List<Person> findAll();

    /**
     * Removes the person with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the person identifier, must not be null
     */
    void deleteById(PersonId id);

    /**
     * Checks whether a person with the given identifier exists.
     *
     * @param id the person identifier, must not be null
     * @return {@code true} if the person exists, {@code false} otherwise
     */
    boolean existsById(PersonId id);
}
