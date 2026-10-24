package com.campus.simulator.observer;

import com.campus.simulator.model.resource.Resource;

import java.time.Instant;

/**
 * Event published when a new Resource is registered in the system.
 */
public record ResourceAddedEvent(Resource resource, Instant occurredAt) implements DomainEvent {

    public ResourceAddedEvent(Resource resource) {
        this(resource, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "ResourceAdded";
    }
}
