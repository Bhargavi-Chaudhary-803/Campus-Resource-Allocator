package com.campus.simulator.observer;

import java.time.Instant;

/**
 * Interface representing a domain event published when key business actions occur.
 */
public interface DomainEvent {

    /**
     * Exact timestamp when this event occurred.
     */
    Instant getOccurredAt();

    /**
     * Human-readable event description or topic name.
     */
    String getEventName();
}
