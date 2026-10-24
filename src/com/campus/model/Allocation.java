package com.campus.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * =========================================================================
 * OOP PRINCIPLE: COMPOSITION
 * =========================================================================
 * Allocation binds a Resource and an AllocationRequest together for an approved TimeSlot.
 */
public class Allocation {

    private final String allocationId;
    private final Resource resource;
    private final AllocationRequest request;
    private final TimeSlot timeSlot;
    private final LocalDateTime allocatedAt;
    private final double totalEstimatedCost;
    private boolean isActive;

    public Allocation(String allocationId, Resource resource, AllocationRequest request, TimeSlot timeSlot) {
        if (allocationId == null || allocationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Allocation ID cannot be empty.");
        }
        this.allocationId = allocationId.trim();
        this.resource = resource;
        this.request = request;
        this.timeSlot = timeSlot;
        this.allocatedAt = LocalDateTime.now();
        this.isActive = true;

        // Polymorphic cost calculation based on resource type & duration
        this.totalEstimatedCost = resource.calculateOperatingCostPerHour() * timeSlot.getDurationHours();
    }

    public String getAllocationId() {
        return allocationId;
    }

    public Resource getResource() {
        return resource;
    }

    public AllocationRequest getRequest() {
        return request;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public LocalDateTime getAllocatedAt() {
        return allocatedAt;
    }

    public double getTotalEstimatedCost() {
        return totalEstimatedCost;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Checks if this active reservation collides with another requested time slot.
     */
    public boolean conflictsWith(TimeSlot otherSlot) {
        return isActive && this.timeSlot.overlaps(otherSlot);
    }

    public void release() {
        this.isActive = false;
        this.resource.release();
    }

    @Override
    public String toString() {
        return String.format("Allocation[%s]: %s -> %s @ %s ($%.2f)",
                allocationId, resource.getName(), request.getRequester().getName(), timeSlot, totalEstimatedCost);
    }
}
