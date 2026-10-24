package com.campus.simulator.service.allocation;

/**
 * Enumeration of supported allocation strategies available in the simulator.
 */
public enum AllocationStrategyType {
    PRIORITY_FIRST("Priority-First Allocation",
            "Allocates based strictly on requester tier and request priority with FIFO tie-breaker."),
    FAIRNESS_AWARE("Fairness-Aware Allocation",
            "Penalizes requesters holding multiple prior allocations to promote equitable distribution."),
    CAPACITY_OPTIMIZED("Capacity-Optimized (Best Fit)",
            "Prefers smallest suitable resource to minimize seat and space waste.");

    private final String displayName;
    private final String description;

    AllocationStrategyType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
