package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLE: ABSTRACTION & INTERFACE
 * =========================================================================
 * The Bookable interface defines the contract for any physical asset on campus
 * that can be checked out, scheduled, or released. Any class implementing this
 * interface must provide concrete behavior for availability tracking.
 */
public interface Bookable {

    /**
     * Checks if the asset is currently available for booking.
     * @return true if available, false if in maintenance or occupied.
     */
    boolean isAvailable();

    /**
     * Attempts to reserve the asset.
     * @return true if booking succeeded.
     */
    boolean book();

    /**
     * Releases the asset back into the available pool.
     */
    void release();

    /**
     * @return Unique ID of the bookable asset.
     */
    String getId();

    /**
     * @return Human-readable name of the asset.
     */
    String getName();
}
