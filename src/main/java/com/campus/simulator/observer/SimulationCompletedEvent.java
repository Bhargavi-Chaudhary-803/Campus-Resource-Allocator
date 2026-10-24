package com.campus.simulator.observer;

import com.campus.simulator.model.allocation.AllocationResult;

import java.time.Instant;
import java.util.List;

/**
 * Event published when a batch simulation completes execution.
 */
public record SimulationCompletedEvent(String strategyName,
                                       List<AllocationResult> results,
                                       Instant occurredAt) implements DomainEvent {

    public SimulationCompletedEvent(String strategyName, List<AllocationResult> results) {
        this(strategyName, results == null ? List.of() : List.copyOf(results), Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "SimulationCompleted";
    }
}
