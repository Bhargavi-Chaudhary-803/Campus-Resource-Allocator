package com.campus.simulator.exception;

/**
 * Thrown when an expected entity (Resource, Requester, Request, or Allocation)
 * is not found in the repository.
 */
public class EntityNotFoundException extends CampusSimulatorException {

    private final String entityType;
    private final String entityId;

    public EntityNotFoundException(String entityType, String entityId) {
        super(String.format("%s with ID '%s' was not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
