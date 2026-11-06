package com.example.opencodetutorial.domain.port;

import com.example.opencodetutorial.domain.model.CenterId;
import com.example.opencodetutorial.domain.model.Course;
import com.example.opencodetutorial.domain.model.CourseId;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for persisting and retrieving {@link Course} aggregates.
 * The infrastructure layer provides the implementation and adapts the chosen
 * persistence technology (JPA + Spring Data in this project) to this interface.
 *
 * <p>Implementations are the only classes allowed to know that courses are
 * stored as JPA entities; the domain layer sees only this abstraction.
 */
public interface CourseRepository {

    /**
     * Persists a course, inserting a new aggregate or updating an existing one.
     *
     * @param course the course to save, must not be null
     * @return the saved course (with its identifier assigned if it was new)
     */
    Course save(Course course);

    /**
     * Looks up a course by its identifier.
     *
     * @param id the course identifier, must not be null
     * @return an {@link Optional} containing the course if found, otherwise empty
     */
    Optional<Course> findById(CourseId id);

    /**
     * Lists every persisted course, in no particular order.
     *
     * @return the list of courses, never null
     */
    List<Course> findAll();

    /**
     * Lists the courses that belong to the given center.
     *
     * @param centerId the owning center identifier, must not be null
     * @return the courses of that center, never null (empty when it has none)
     */
    List<Course> findByCenterId(CenterId centerId);

    /**
     * Removes the course with the given identifier. No-op if the identifier is unknown.
     *
     * @param id the course identifier, must not be null
     */
    void deleteById(CourseId id);

    /**
     * Checks whether a course with the given identifier exists.
     *
     * @param id the course identifier, must not be null
     * @return {@code true} if the course exists, {@code false} otherwise
     */
    boolean existsById(CourseId id);
}