package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.Group;
import com.example.opencodetutorial.domain.model.GroupId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Group} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that groups are
 * stored as JPA entities; the domain layer sees only this abstraction.
 */
public interface GroupRepository {

    /**
     * Persists a group, inserting a new aggregate or updating an existing one.
     *
     * @param group the group to save, must not be null
     * @return the saved group (with its identifier assigned if it was new)
     */
    Group save(Group group);

    /**
     * Looks up a group by its identifier.
     *
     * @param id the group identifier, must not be null
     * @return an {@link Optional} containing the group if found, otherwise empty
     */
    Optional<Group> findById(GroupId id);

    /**
     * Lists every persisted group, in no particular order.
     *
     * @return the list of groups, never null
     */
    List<Group> findAll();

    /**
     * Removes the group with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the group identifier, must not be null
     */
    void deleteById(GroupId id);

    /**
     * Checks whether a group with the given identifier exists.
     *
     * @param id the group identifier, must not be null
     * @return {@code true} if the group exists, {@code false} otherwise
     */
    boolean existsById(GroupId id);
}
