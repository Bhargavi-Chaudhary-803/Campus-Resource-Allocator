package com.campus.simulator.observer;

import com.campus.simulator.model.requester.Requester;

import java.time.Instant;

/**
 * Event published when a new Requester (department, club, faculty, event) is enrolled.
 */
public record RequesterAddedEvent(Requester requester, Instant occurredAt) implements DomainEvent {

    public RequesterAddedEvent(Requester requester) {
        this(requester, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "RequesterAdded";
    }
}
