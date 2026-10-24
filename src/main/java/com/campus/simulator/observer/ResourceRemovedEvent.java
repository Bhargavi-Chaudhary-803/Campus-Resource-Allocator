package com.campus.simulator.observer;

import java.time.Instant;

/**
 * Event published when a resource is deleted or removed from active inventory.
 */
public record ResourceRemovedEvent(String resourceId, Instant occurredAt) implements DomainEvent {

    public ResourceRemovedEvent(String resourceId) {
        this(resourceId, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "ResourceRemoved";
    }
}
