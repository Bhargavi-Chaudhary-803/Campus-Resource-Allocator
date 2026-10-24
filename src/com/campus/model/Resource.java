package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: ABSTRACTION & ENCAPSULATION
 * =========================================================================
 * Abstract base class representing any campus facility or physical asset.
 * Demonstrates:
 * 1. Data Encapsulation: fields are private and accessed via validated methods.
 * 2. Abstraction: Defines abstract methods that concrete subclasses must implement.
 * 3. Interface Implementation: Fulfills the Bookable contract.
 */
public abstract class Resource implements Bookable {

    // [OOP - ENCAPSULATION]: Private fields protected from direct external modification
    private final String id;
    private String name;
    private String location;
    private int capacity;
    private final ResourceType type;
    private boolean isUnderMaintenance;
    private boolean isCurrentlyBooked;

    public Resource(String id, String name, String location, int capacity, ResourceType type) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource ID cannot be null or empty.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Resource capacity must be greater than zero.");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.location = location.trim();
        this.capacity = capacity;
        this.type = type;
        this.isUnderMaintenance = false;
        this.isCurrentlyBooked = false;
    }

    // [OOP - ENCAPSULATION]: Getters and Setters with validation
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location != null && !location.trim().isEmpty()) {
            this.location = location.trim();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }

    public ResourceType getType() {
        return type;
    }

    public boolean isUnderMaintenance() {
        return isUnderMaintenance;
    }

    public boolean isBooked() {
        return isCurrentlyBooked;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.isUnderMaintenance = underMaintenance;
    }

    /**
     * Checks if the resource can accommodate the specified number of attendees/seats.
     */
    public boolean canAccommodate(int attendees) {
        return this.capacity >= attendees;
    }

    // Implementation of Bookable interface
    @Override
    public boolean isAvailable() {
        return !isUnderMaintenance && !isCurrentlyBooked;
    }

    @Override
    public boolean book() {
        if (isAvailable()) {
            this.isCurrentlyBooked = true;
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        this.isCurrentlyBooked = false;
    }

    // =========================================================================
    // [OOP - ABSTRACTION & POLYMORPHISM]: Abstract methods
    // Every concrete subclass must provide its own implementation!
    // =========================================================================
    public abstract String getDetails();

    public abstract double calculateOperatingCostPerHour();

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, Cap: %d)", id, name, type.getDisplayName(), capacity);
    }
}
