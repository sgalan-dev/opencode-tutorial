package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Professor;
import com.example.opencodetutorial.domain.model.ProfessorId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Professor} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that professors are
 * stored as JPA entities; the domain layer sees only this abstraction.
 */
public interface ProfessorRepository {

    /**
     * Persists a professor, inserting a new aggregate or updating an existing one.
     *
     * @param professor the professor to save, must not be null
     * @return the saved professor (with its identifier assigned if it was new)
     */
    Professor save(Professor professor);

    /**
     * Looks up a professor by its identifier.
     *
     * @param id the professor identifier, must not be null
     * @return an {@link Optional} containing the professor if found, otherwise empty
     */
    Optional<Professor> findById(ProfessorId id);

    /**
     * Lists every persisted professor, in no particular order.
     *
     * @return the list of professors, never null
     */
    List<Professor> findAll();

    /**
     * Lists the professors that belong to the given center.
     *
     * @param centerId the owning center identifier, must not be null
     * @return the professors of that center, never null (empty when it has none)
     */
    List<Professor> findByCenterId(CenterId centerId);

    /**
     * Removes the professor with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the professor identifier, must not be null
     */
    void deleteById(ProfessorId id);

    /**
     * Checks whether a professor with the given identifier exists.
     *
     * @param id the professor identifier, must not be null
     * @return {@code true} if the professor exists, {@code false} otherwise
     */
    boolean existsById(ProfessorId id);
}
