package com.campus.simulator.observer;

import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;

import java.time.Instant;

/**
 * Event published when a request changes status (e.g. APPROVED, WAITLISTED, REJECTED, CANCELLED).
 */
public record RequestStatusChangedEvent(ResourceRequest request,
                                        RequestStatus oldStatus,
                                        RequestStatus newStatus,
                                        String reason,
                                        Instant occurredAt) implements DomainEvent {

    public RequestStatusChangedEvent(ResourceRequest request, RequestStatus oldStatus, RequestStatus newStatus, String reason) {
        this(request, oldStatus, newStatus, reason, Instant.now());
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getEventName() {
        return "RequestStatusChanged";
    }
}
