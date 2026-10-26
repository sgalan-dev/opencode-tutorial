package com.example.opencodetutorial.domain.exception;

/**
 * Thrown when a resource name is already taken. The REST layer maps this exception to
 * HTTP 409: center names are unique system-wide and course names are unique within
 * their center.
 */
public final class DuplicateNameException extends RuntimeException {

    private final String resourceType;
    private final String name;

    /**
     * Builds a "name already in use" exception.
     *
     * @param resourceType the kind of resource whose name collides, must not be null
     * @param name         the offending name, must not be null
     * @param scope        a human-readable description of the uniqueness scope
     */
    public DuplicateNameException(String resourceType, String name, String scope) {
        super("A " + resourceType + " named '" + name + "' already exists " + scope);
        this.resourceType = resourceType;
        this.name = name;
    }

    public String resourceType() {
        return resourceType;
    }

    public String name() {
        return name;
    }
}