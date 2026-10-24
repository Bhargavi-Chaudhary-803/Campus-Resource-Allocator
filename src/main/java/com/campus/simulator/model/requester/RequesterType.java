package com.campus.simulator.model.requester;

/**
 * Categorization of campus actors eligible to submit allocation requests.
 */
public enum RequesterType {
    DEPARTMENT("Academic Department"),
    FACULTY_MEMBER("Faculty Member"),
    STUDENT_CLUB("Student Club"),
    EVENT_ORGANIZER("Event Organizer");

    private final String displayName;

    RequesterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
