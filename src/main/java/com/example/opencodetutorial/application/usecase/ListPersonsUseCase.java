package com.example.opencodetutorial.application.usecase;

import com.example.opencodetutorial.domain.model.Person;

import java.util.List;

/**
 * Inbound port for the "list persons" use case.
 */
public interface ListPersonsUseCase {

    /**
     * Returns every persisted person, in no particular order.
     *
     * @return the list of persons, never null
     */
    List<Person> execute();
}
