package com.example.opencodetutorial.domain.exception;

/**
 * Thrown when a resource cannot be deleted because other resources depend on it, for
 * example a center that still owns courses or students. The REST layer maps this
 * exception to HTTP 409 so the caller learns the deletion was rejected instead of
 * leaving partial state behind.
 */
public final class ResourceHasDependentsException extends RuntimeException {

    private final String resourceType;
    private final Long resourceId;

    /**
     * Builds a "resource has dependents" exception.
     *
     * @param resourceType the kind of resource that cannot be deleted, must not be null
     * @param resourceId   the identifier of that resource, must not be null
     * @param dependent    a human-readable description of the blocking dependents,
     *                     used to build the message
     */
    public ResourceHasDependentsException(String resourceType, Long resourceId, String dependent) {
        super(resourceType + " with id " + resourceId + " cannot be deleted because it still has "
                + dependent);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String resourceType() {
        return resourceType;
    }

    public Long resourceId() {
        return resourceId;
    }
}