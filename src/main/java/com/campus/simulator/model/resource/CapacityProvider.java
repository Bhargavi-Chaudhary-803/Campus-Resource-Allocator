package com.campus.simulator.model.resource;

/**
 * Capability interface for resources with a quantifiable passenger, student, or equipment capacity.
 */
public interface CapacityProvider {

    /**
     * Maximum capacity provided by this resource (e.g. seats, workstations, devices).
     */
    int getCapacity();

    /**
     * Determines whether this resource can accommodate the specified number of occupants or items.
     */
    default boolean canAccommodate(int requiredCapacity) {
        return getCapacity() >= requiredCapacity;
    }
}
