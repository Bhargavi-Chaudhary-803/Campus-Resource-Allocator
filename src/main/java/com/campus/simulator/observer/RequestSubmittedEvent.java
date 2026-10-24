package com.campus.simulator.observer;

import com.campus.simulator.model.allocation.ResourceRequest;

import java.time.Instant;

/**
 * Event published when a new allocation request is submitted.
 */
public record RequestSubmittedEvent(ResourceRequest request, Instant occurredAt) implements DomainEvent {

    public RequestSubmittedEvent(ResourceRequest request) {
        this(request, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "RequestSubmitted";
    }
}
