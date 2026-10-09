package com.example.opencodetutorial.infrastructure.adapter.output.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JPA entity for the {@code student} table plus its many-to-many link to groups.
 *
 * <p>The {@code Set<Long> groupIds} is mapped with {@link ElementCollection} onto the
 * junction table {@code student_group_link}. The junction has no aggregate of its own:
 * it is owned by {@code Student} and managed entirely by {@code StudentPersistenceAdapter}.
 */
@Entity
@Table(name = "student")
public class StudentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "student_group_link",
            joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "group_id", nullable = false)
    private Set<Long> groupIds = new LinkedHashSet<>();

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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Set<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Set<Long> groupIds) {
        this.groupIds = groupIds;
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
