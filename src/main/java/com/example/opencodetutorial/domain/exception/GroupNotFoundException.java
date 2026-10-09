package com.example.opencodetutorial.domain.exception;

import com.example.opencodetutorial.domain.model.GroupId;

/**
 * Thrown when a {@link com.example.opencodetutorial.domain.model.Group} cannot be
 * found by the supplied identifier. The REST layer maps this exception to HTTP 404.
 */
public final class GroupNotFoundException extends RuntimeException {

    private final GroupId id;

    /**
     * Builds a "group not found" exception for the given identifier.
     *
     * @param id the missing group's identifier, must not be null
     */
    public GroupNotFoundException(GroupId id) {
        super("Group with id " + id.value() + " was not found");
        this.id = id;
    }

    public GroupId id() {
        return id;
    }
}
