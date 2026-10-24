package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * Auditorium inherits from Resource. Used for large keynotes, cultural events,
 * and conferences with massive seating capacities.
 */
public class Auditorium extends Resource {

    private final boolean hasProAudioSystem;
    private final boolean hasStage;
    private final String stageArea;

    public Auditorium(String id, String name, String location, int capacity,
                      boolean hasProAudioSystem, boolean hasStage, String stageArea) {
        super(id, name, location, capacity, ResourceType.AUDITORIUM);
        this.hasProAudioSystem = hasProAudioSystem;
        this.hasStage = hasStage;
        this.stageArea = stageArea;
    }

    public boolean hasProAudioSystem() {
        return hasProAudioSystem;
    }

    public boolean hasStage() {
        return hasStage;
    }

    public String getStageArea() {
        return stageArea;
    }

    // [OOP - POLYMORPHISM]: Overriding abstract method
    @Override
    public String getDetails() {
        return String.format("Stage: %s (%s) | Pro Audio: %s",
                hasStage ? "Yes" : "No",
                stageArea,
                hasProAudioSystem ? "Active" : "None");
    }

    // [OOP - POLYMORPHISM]: Auditoriums require heavy power & air handling
    @Override
    public double calculateOperatingCostPerHour() {
        double cost = 80.0;
        if (hasProAudioSystem) cost += 25.0;
        return cost;
    }
}
