package com.campus.model;

/**
 * =========================================================================
 * JAVA CONCEPT: ENUMERATIONS (Type Safety)
 * =========================================================================
 * Enumerates categories of physical campus resources available in the system.
 */
public enum ResourceType {
    CLASSROOM("Lecture Classroom"),
    LABORATORY("Scientific / Computer Lab"),
    AUDITORIUM("Main Auditorium"),
    EQUIPMENT("Portable Equipment");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
