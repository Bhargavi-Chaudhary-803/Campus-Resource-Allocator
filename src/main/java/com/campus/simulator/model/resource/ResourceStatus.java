package com.campus.simulator.model.resource;

/**
 * Operational lifecycle status of a campus resource.
 */
public enum ResourceStatus {
    AVAILABLE("Available for Booking"),
    MAINTENANCE("Under Maintenance"),
    RETIRED("Decommissioned / Retired");

    private final String displayName;

    ResourceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOperational() {
        return this == AVAILABLE;
    }
}
