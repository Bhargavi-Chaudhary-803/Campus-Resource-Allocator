package com.campus.simulator.model.resource;

/**
 * Capability interface for movable hardware that can be checked out or transferred across campus.
 */
public interface PortableResource {

    /**
     * Whether the hardware can be transported by hand or mobile cart.
     */
    boolean isPortable();

    /**
     * Physical storage or docking location when not checked out.
     */
    String getStorageLocation();
}
