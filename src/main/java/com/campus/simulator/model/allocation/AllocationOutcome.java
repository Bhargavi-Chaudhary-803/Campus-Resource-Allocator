package com.campus.simulator.model.allocation;

/**
 * Result classification returned after attempting to allocate a request.
 */
public enum AllocationOutcome {
    APPROVED("Approved"),
    WAITLISTED("Waitlisted"),
    REJECTED("Rejected");

    private final String displayName;

    AllocationOutcome(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
