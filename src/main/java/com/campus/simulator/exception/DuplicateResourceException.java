package com.campus.simulator.exception;

/**
 * Thrown when attempting to register a resource or entity with an ID that already exists.
 */
public class DuplicateResourceException extends CampusSimulatorException {

    private final String resourceId;

    public DuplicateResourceException(String resourceId) {
        super(String.format("Resource with ID '%s' already exists", resourceId));
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
