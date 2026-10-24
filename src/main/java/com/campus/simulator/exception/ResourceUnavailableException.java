package com.campus.simulator.exception;

/**
 * Thrown when an operation targets a resource that is currently under maintenance or retired.
 */
public class ResourceUnavailableException extends CampusSimulatorException {

    private final String resourceId;

    public ResourceUnavailableException(String resourceId, String message) {
        super(String.format("Resource [%s] is unavailable: %s", resourceId, message));
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }
}
