package com.campus.model;

/**
 * =========================================================================
 * JAVA CONCEPT: ENUMERATIONS
 * =========================================================================
 * State machine status of an allocation request.
 */
public enum RequestStatus {
    PENDING("Pending"),
    ALLOCATED("Allocated"),
    WAITLISTED("Waitlisted"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
