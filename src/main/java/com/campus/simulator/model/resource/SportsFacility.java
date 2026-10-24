package com.campus.simulator.model.resource;

import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Resource representing an athletic field, gymnasium, or court.
 */
public class SportsFacility extends Resource {

    private final String sportType;
    private final boolean outdoor;

    public SportsFacility(String id,
                          String name,
                          String location,
                          int capacity,
                          String sportType,
                          boolean outdoor) {
        super(id, name, location, capacity, ResourceType.SPORTS_FACILITY);
        this.sportType = ValidationUtils.requireNonBlank(sportType, "sportType");
        this.outdoor = outdoor;
    }

    public String getSportType() {
        return sportType;
    }

    public boolean isOutdoor() {
        return outdoor;
    }

    @Override
    public String getSpecificDetailsSummary() {
        return String.format("Sport: %s | Environment: %s",
                sportType,
                outdoor ? "Outdoor" : "Indoor");
    }
}
