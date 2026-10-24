package com.campus.simulator.observer;

/**
 * Functional interface for listeners subscribing to domain events.
 */
@FunctionalInterface
public interface DomainEventListener {

    /**
     * Callback invoked when a domain event is published.
     */
    void onEvent(DomainEvent event);
}
