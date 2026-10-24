package com.campus.model;

/**
 * =========================================================================
 * JAVA CONCEPT: ENUMERATIONS
 * =========================================================================
 * Categorization of entities requesting campus facilities.
 */
public enum RequesterType {
    DEPARTMENT("Academic Department"),
    FACULTY("Faculty Member"),
    STUDENT_CLUB("Student Club / Society");

    private final String label;

    RequesterType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplayName() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
