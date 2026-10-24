package com.campus.simulator.model.allocation;

/**
 * State lifecycle of a submitted resource request.
 */
public enum RequestStatus {
    PENDING("Pending Simulation"),
    APPROVED("Approved & Allocated"),
    WAITLISTED("Waitlisted (No Resource Free)"),
    REJECTED("Rejected (Policy / Incompatible)"),
    CANCELLED("Cancelled by Requester"),
    COMPLETED("Completed / Concluded");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isTerminal() {
        return this == CANCELLED || this == COMPLETED;
    }
}
