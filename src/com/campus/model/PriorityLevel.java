package com.campus.model;

/**
 * =========================================================================
 * JAVA CONCEPT: ENUMERATIONS
 * =========================================================================
 * Priority level assigned to requests and requester tiers.
 */
public enum PriorityLevel {
    LOW(1, "Low Priority"),
    NORMAL(2, "Normal Priority"),
    HIGH(3, "High Priority"),
    URGENT(4, "Urgent / Institutional");

    private final int value;
    private final String label;

    PriorityLevel(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
