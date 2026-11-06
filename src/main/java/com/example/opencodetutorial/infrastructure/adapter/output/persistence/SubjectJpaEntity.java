package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * JPA entity for the {@code subject} table.
 *
 * <p>The table keeps its natural name because {@code SUBJECT} is not a reserved SQL
 * keyword, unlike {@code GROUP}, which is why the group table is {@code student_group}.
 *
 * <p>{@code courseId} is stored as a plain column instead of a {@code @ManyToOne}
 * association: the domain only ever needs the identifier, and keeping it scalar avoids
 * loading the course row when a subject is read.
 *
 * <p>The professor that teaches the subject is NOT a column of this table: it lives in
 * {@link SubjectTeachingJpaEntity}, the table that materialises the relation between
 * course, subject and professor.
 */
@Entity
@Table(name = "subject")
public class SubjectJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
