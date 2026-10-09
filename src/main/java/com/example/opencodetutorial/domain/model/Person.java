package com.example.opencodetutorial.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Person aggregate root.
 * Encapsulates the rules that govern a person's lifecycle (renaming) and
 * exposes them as behaviour methods rather than setters.
 *
 * <p>The {@link #id()} is {@code null} while the aggregate is in the "just created"
 * state and gets assigned by the persistence layer on first save. Once rehydrated
 * (via {@link #rehydrate}) the id is always present.
 *
 * <p>A person has a mandatory {@link PersonSurname primary surname} and an OPTIONAL
 * {@link SecondSurname second surname}. The optional nature of the second surname
 * is modelled at the value-object level (the constructor accepts {@code null}), so
 * the aggregate itself never needs nullable primitives.
 *
 * <p>This class is part of the pure domain layer and MUST NOT depend on any framework
 * (Spring, JPA, Jackson, validation, etc.). Persistence concerns are handled by a
 * dedicated adapter in the infrastructure layer that translates between this aggregate
 * and a JPA entity.
 */
public final class Person {

    private final PersonId id;
    private final Instant createdAt;
    private PersonName name;
    private PersonSurname surname;
    private SecondSurname surname2;
    private Instant updatedAt;

    private Person(PersonId id,
                   PersonName name,
                   PersonSurname surname,
                   SecondSurname surname2,
                   Instant createdAt,
                   Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.surname2 = surname2;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates a brand-new {@link Person} with no identifier yet (the persistence layer
     * will assign one on first save). Use this from use cases when handling a
     * "create person" command.
     *
     * @param name      the initial given name, must not be null
     * @param surname   the initial primary surname, must not be null
     * @param surname2  the initial second surname, may be {@code null} when the person
     *                  has no second surname
     * @return a new person in the "just created" state
     */
    public static Person create(PersonName name, PersonSurname surname, SecondSurname surname2) {
        Instant now = Instant.now();
        return new Person(null, name, surname, surname2, now, now);
    }

    /**
     * Rehydrates a {@link Person} from persistence, preserving its original identity
     * and timestamps. This factory is the only legitimate way to reconstruct an
     * existing person from outside the domain.
     *
     * @param id        the existing identifier, must not be null
     * @param name      the stored given name, must not be null
     * @param surname   the stored primary surname, must not be null
     * @param surname2  the stored second surname, may be {@code null}
     * @param createdAt the original creation timestamp, must not be null
     * @param updatedAt the last update timestamp, must not be null
     * @return the reconstructed person
     */
    public static Person rehydrate(PersonId id,
                                   PersonName name,
                                   PersonSurname surname,
                                   SecondSurname surname2,
                                   Instant createdAt,
                                   Instant updatedAt) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(surname, "surname must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        return new Person(id, name, surname, surname2, createdAt, updatedAt);
    }

    /**
     * Updates the person's given name.
     *
     * @param newName the new name, must not be null
     */
    public void rename(PersonName newName) {
        this.name = Objects.requireNonNull(newName, "name must not be null");
        touch();
    }

    /**
     * Updates the person's primary surname.
     *
     * @param newSurname the new primary surname, must not be null
     */
    public void changeSurname(PersonSurname newSurname) {
        this.surname = Objects.requireNonNull(newSurname, "surname must not be null");
        touch();
    }

    /**
     * Updates the person's second surname. Pass {@code null} to clear it.
     *
     * @param newSurname2 the new second surname, or {@code null} to remove it
     */
    public void changeSecondSurname(SecondSurname newSurname2) {
        this.surname2 = newSurname2;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public PersonId id() {
        return id;
    }

    public PersonName name() {
        return name;
    }

    public PersonSurname surname() {
        return surname;
    }

    public SecondSurname surname2() {
        return surname2;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
