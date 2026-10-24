package com.campus.simulator.observer;

import com.campus.simulator.model.allocation.Allocation;

import java.time.Instant;

/**
 * Event published when a resource is successfully allocated to a request.
 */
public record AllocationApprovedEvent(Allocation allocation, Instant occurredAt) implements DomainEvent {

    public AllocationApprovedEvent(Allocation allocation) {
        this(allocation, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "AllocationApproved";
    }
}
