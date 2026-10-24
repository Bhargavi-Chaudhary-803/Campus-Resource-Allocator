package com.campus.simulator.model.resource;

/**
 * Categories of physical resources available across the campus.
 */
public enum ResourceType {
    CLASSROOM("Classroom"),
    LABORATORY("Laboratory"),
    PROJECTOR("Projector"),
    LAPTOP_SET("Laptop Set"),
    SPORTS_FACILITY("Sports Facility"),
    STUDY_ROOM("Study Room");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
