package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * Classroom inherits common fields and methods from Resource and overrides
 * abstract methods to provide specific classroom behaviors.
 */
public class Classroom extends Resource {

    private final String roomNumber;
    private final boolean hasProjector;
    private final boolean hasSmartBoard;

    public Classroom(String id, String name, String location, int capacity,
                     String roomNumber, boolean hasProjector, boolean hasSmartBoard) {
        // [OOP - INHERITANCE]: Invoking the superclass constructor
        super(id, name, location, capacity, ResourceType.CLASSROOM);
        this.roomNumber = roomNumber;
        this.hasProjector = hasProjector;
        this.hasSmartBoard = hasSmartBoard;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public boolean hasProjector() {
        return hasProjector;
    }

    public boolean hasSmartBoard() {
        return hasSmartBoard;
    }

    // [OOP - POLYMORPHISM]: Overriding abstract method from Resource
    @Override
    public String getDetails() {
        return String.format("Room: %s | Projector: %s | SmartBoard: %s",
                roomNumber,
                hasProjector ? "Yes" : "No",
                hasSmartBoard ? "Yes" : "No");
    }

    // [OOP - POLYMORPHISM]: Dynamic cost calculation based on classroom amenities
    @Override
    public double calculateOperatingCostPerHour() {
        double baseRate = 15.0; // Base electricity & HVAC
        if (hasProjector) baseRate += 5.0;
        if (hasSmartBoard) baseRate += 10.0;
        return baseRate;
    }
}
