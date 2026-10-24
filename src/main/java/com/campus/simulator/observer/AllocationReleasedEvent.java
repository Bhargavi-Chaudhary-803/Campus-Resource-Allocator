package com.campus.simulator.observer;

import com.campus.simulator.model.allocation.Allocation;

import java.time.Instant;

/**
 * Event published when an allocation is released early or cancelled.
 */
public record AllocationReleasedEvent(Allocation allocation, Instant occurredAt) implements DomainEvent {

    public AllocationReleasedEvent(Allocation allocation) {
        this(allocation, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "AllocationReleased";
    }
}
