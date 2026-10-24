package com.campus.simulator.exception;

/**
 * Thrown when an allocation cannot be performed due to schedule collisions
 * or concurrent reservations on the target resource.
 */
public class AllocationConflictException extends CampusSimulatorException {

    private final String resourceId;
    private final String conflictDetails;

    public AllocationConflictException(String resourceId, String conflictDetails) {
        super(String.format("Allocation conflict on resource [%s]: %s", resourceId, conflictDetails));
        this.resourceId = resourceId;
        this.conflictDetails = conflictDetails;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getConflictDetails() {
        return conflictDetails;
    }
}
