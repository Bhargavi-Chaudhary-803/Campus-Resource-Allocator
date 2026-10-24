package com.campus.simulator.model.resource;

import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Resource representing a lecture hall or traditional teaching classroom.
 */
public class Classroom extends Resource {

    private final String roomNumber;
    private final boolean hasWhiteboard;
    private final boolean hasProjectorScreen;

    public Classroom(String id,
                     String name,
                     String location,
                     int capacity,
                     String roomNumber,
                     boolean hasWhiteboard,
                     boolean hasProjectorScreen) {
        super(id, name, location, capacity, ResourceType.CLASSROOM);
        this.roomNumber = ValidationUtils.requireNonBlank(roomNumber, "roomNumber");
        this.hasWhiteboard = hasWhiteboard;
        this.hasProjectorScreen = hasProjectorScreen;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public boolean hasWhiteboard() {
        return hasWhiteboard;
    }

    public boolean hasProjectorScreen() {
        return hasProjectorScreen;
    }

    @Override
    public String getSpecificDetailsSummary() {
        return String.format("Room: %s | Whiteboard: %s | Screen: %s",
                roomNumber,
                hasWhiteboard ? "Yes" : "No",
                hasProjectorScreen ? "Yes" : "No");
    }
}
