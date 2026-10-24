package com.campus.simulator.model.allocation;

/**
 * Operational status of an active or finalized allocation.
 */
public enum AllocationStatus {
    ACTIVE("Active Reservation"),
    RELEASED("Released Ahead of Schedule"),
    COMPLETED("Completed Successfully"),
    CANCELLED("Cancelled");

    private final String displayName;

    AllocationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
