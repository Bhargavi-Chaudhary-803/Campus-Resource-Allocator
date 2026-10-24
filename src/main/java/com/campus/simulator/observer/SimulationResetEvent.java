package com.campus.simulator.observer;

import java.time.Instant;

/**
 * Event published when the simulator is reseeded with baseline demo data.
 */
public record SimulationResetEvent(Instant occurredAt) implements DomainEvent {

    public SimulationResetEvent() {
        this(Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "SimulationReset";
    }
}
