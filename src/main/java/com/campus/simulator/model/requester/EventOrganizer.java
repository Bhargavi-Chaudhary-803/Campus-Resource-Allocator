package com.campus.simulator.model.requester;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Requester representing campus event committees, symposium chairs, or festival directors.
 */
public class EventOrganizer extends Requester {

    private final String eventType;
    private final int expectedAttendance;

    public EventOrganizer(String id,
                          String name,
                          String contactEmail,
                          PriorityTier priorityTier,
                          String eventType,
                          int expectedAttendance) {
        super(id, name, contactEmail, RequesterType.EVENT_ORGANIZER, priorityTier);
        this.eventType = ValidationUtils.requireNonBlank(eventType, "eventType");
        this.expectedAttendance = ValidationUtils.requirePositive(expectedAttendance, "expectedAttendance");
    }

    public String getEventType() {
        return eventType;
    }

    public int getExpectedAttendance() {
        return expectedAttendance;
    }

    @Override
    public int getAllocationPriority() {
        // Base 50, attendance boost (up to +15), plus priority tier ranking (1..4 * 5 = 5..20)
        int attendanceBonus = Math.min(15, expectedAttendance / 20);
        return 50 + attendanceBonus + (getPriorityTier().getRank() * 5);
    }

    @Override
    public String getDescription() {
        return String.format("%s (Est. Attendance: %d)", eventType, expectedAttendance);
    }

    @Override
    public boolean canRequestResource(Resource resource) {
        return resource != null;
    }

    @Override
    public double calculateRequestWeight() {
        return 1.3;
    }
}
