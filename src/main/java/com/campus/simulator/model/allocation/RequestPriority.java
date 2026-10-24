package com.campus.simulator.model.allocation;

/**
 * Priority levels assignable to specific resource allocation requests.
 */
public enum RequestPriority {
    LOW(1, "Low"),
    NORMAL(2, "Normal"),
    HIGH(3, "High"),
    URGENT(4, "Urgent");

    private final int rank;
    private final String displayName;

    RequestPriority(int rank, String displayName) {
        this.rank = rank;
        this.displayName = displayName;
    }

    public int getRank() {
        return rank;
    }

    public String getDisplayName() {
        return displayName;
    }
}
