package com.campus.simulator.observer;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceStatus;

import java.time.Instant;

/**
 * Event published when a resource's status changes (e.g. into maintenance or restored to available).
 */
public record ResourceStatusChangedEvent(Resource resource,
                                         ResourceStatus oldStatus,
                                         ResourceStatus newStatus,
                                         Instant occurredAt) implements DomainEvent {

    public ResourceStatusChangedEvent(Resource resource, ResourceStatus oldStatus, ResourceStatus newStatus) {
        this(resource, oldStatus, newStatus, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "ResourceStatusChanged";
    }
}
