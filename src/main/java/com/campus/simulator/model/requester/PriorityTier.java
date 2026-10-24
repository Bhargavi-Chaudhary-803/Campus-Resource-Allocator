package com.campus.simulator.model.requester;

/**
 * Priority tiers assigned to requesters based on university governance and urgency.
 */
public enum PriorityTier {
    LOW(1, "Low Priority (Casual / Informal)"),
    MEDIUM(2, "Medium Priority (Student Life / Electives)"),
    HIGH(3, "High Priority (Departmental / Academic Core)"),
    CRITICAL(4, "Critical Priority (Institutional / Accreditation)");

    private final int rank;
    private final String description;

    PriorityTier(int rank, String description) {
        this.rank = rank;
        this.description = description;
    }

    public int getRank() {
        return rank;
    }

    public String getDescription() {
        return description;
    }
}
