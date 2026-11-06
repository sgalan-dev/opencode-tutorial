package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.Center;
import com.example.opencodetutorial.domain.model.CenterId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Center} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that centers are
 * stored as JPA entities; the domain layer sees only this abstraction.
 */
public interface CenterRepository {

    /**
     * Persists a center, inserting a new aggregate or updating an existing one.
     *
     * @param center the center to save, must not be null
     * @return the saved center (with its identifier assigned if it was new)
     */
    Center save(Center center);

    /**
     * Looks up a center by its identifier.
     *
     * @param id the center identifier, must not be null
     * @return an {@link Optional} containing the center if found, otherwise empty
     */
    Optional<Center> findById(CenterId id);

    /**
     * Lists every persisted center, in no particular order.
     *
     * @return the list of centers, never null
     */
    List<Center> findAll();

    /**
     * Removes the center with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the center identifier, must not be null
     */
    void deleteById(CenterId id);

    /**
     * Checks whether a center with the given identifier exists.
     *
     * @param id the center identifier, must not be null
     * @return {@code true} if the center exists, {@code false} otherwise
     */
    boolean existsById(CenterId id);
}