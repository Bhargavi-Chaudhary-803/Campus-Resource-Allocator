package com.campus.simulator.model.resource;

/**
 * Capability interface for assets requiring scheduled servicing, maintenance, or decommissioning.
 */
public interface Maintainable {

    /**
     * Places the asset under maintenance.
     */
    void markUnderMaintenance(String reason);

    /**
     * Restores the asset back to available status.
     */
    void markAvailable();

    /**
     * Retires the asset from active service permanently.
     */
    void retire(String reason);

    /**
     * Returns the current operational status.
     */
    ResourceStatus getStatus();
}
