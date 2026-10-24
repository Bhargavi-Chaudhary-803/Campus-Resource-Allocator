package com.campus.simulator.model.resource;

/**
 * Capability interface representing an asset that can be booked or scheduled.
 */
public interface Bookable {

    /**
     * Checks if this asset is currently open for booking.
     */
    boolean isBookable();

    /**
     * Unique identifier of the bookable asset.
     */
    String getId();

    /**
     * Display name of the bookable asset.
     */
    String getName();
}
