package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.Student;
import com.example.opencodetutorial.domain.model.StudentId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Student} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that students (and
 * their many-to-many links to groups) are stored as JPA entities; the domain
 * layer sees only this abstraction.
 */
public interface StudentRepository {

    /**
     * Persists a student, inserting a new aggregate or updating an existing one.
     *
     * @param student the student to save, must not be null
     * @return the saved student (with its identifier assigned if it was new)
     */
    Student save(Student student);

    /**
     * Looks up a student by its identifier.
     *
     * @param id the student identifier, must not be null
     * @return an {@link Optional} containing the student if found, otherwise empty
     */
    Optional<Student> findById(StudentId id);

    /**
     * Lists every persisted student, in no particular order.
     *
     * @return the list of students, never null
     */
    List<Student> findAll();

    /**
     * Removes the student with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the student identifier, must not be null
     */
    void deleteById(StudentId id);

    /**
     * Checks whether a student with the given identifier exists.
     *
     * @param id the student identifier, must not be null
     * @return {@code true} if the student exists, {@code false} otherwise
     */
    boolean existsById(StudentId id);
}
