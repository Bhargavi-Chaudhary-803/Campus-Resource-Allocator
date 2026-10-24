package com.campus.simulator.model.allocation;

import com.campus.simulator.exception.InvalidRequestException;
import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.util.ValidationUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Entity representing an official submission for campus resource allocation.
 * Demonstrates encapsulation, domain event-readiness, and controlled state machine transitions.
 */
public class ResourceRequest {

    private final String id;
    private final Requester requester;
    private final ResourceType resourceType;
    private final String targetResourceId;
    private final TimeSlot requestedTimeSlot;
    private final int requiredCapacity;
    private final RequestPriority priority;
    private final String purpose;
    private final LocalDateTime createdAt;

    private RequestStatus status;
    private String statusReason;

    public ResourceRequest(String id,
                           Requester requester,
                           ResourceType resourceType,
                           String targetResourceId,
                           TimeSlot requestedTimeSlot,
                           int requiredCapacity,
                           RequestPriority priority,
                           String purpose) {
        this(id, requester, resourceType, targetResourceId, requestedTimeSlot, requiredCapacity,
                priority, purpose, RequestStatus.PENDING, null, LocalDateTime.now());
    }

    public ResourceRequest(String id,
                           Requester requester,
                           ResourceType resourceType,
                           String targetResourceId,
                           TimeSlot requestedTimeSlot,
                           int requiredCapacity,
                           RequestPriority priority,
                           String purpose,
                           RequestStatus status,
                           String statusReason,
                           LocalDateTime createdAt) {
        this.id = ValidationUtils.requireNonBlank(id, "id");
        this.requester = ValidationUtils.requireNonNull(requester, "requester");
        if (!requester.isActive()) {
            throw new InvalidRequestException(id, "Requester [" + requester.getName() + "] is inactive");
        }
        this.resourceType = ValidationUtils.requireNonNull(resourceType, "resourceType");
        this.targetResourceId = (targetResourceId != null && !targetResourceId.isBlank())
                ? targetResourceId.trim() : null;
        this.requestedTimeSlot = ValidationUtils.requireNonNull(requestedTimeSlot, "requestedTimeSlot");
        this.requiredCapacity = ValidationUtils.requirePositive(requiredCapacity, "requiredCapacity");
        this.priority = ValidationUtils.requireNonNull(priority, "priority");
        this.purpose = ValidationUtils.requireNonBlank(purpose, "purpose");
        this.status = ValidationUtils.requireNonNull(status, "status");
        this.statusReason = statusReason;
        this.createdAt = ValidationUtils.requireNonNull(createdAt, "createdAt");
    }

    public String getId() {
        return id;
    }

    public Requester getRequester() {
        return requester;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public Optional<String> getTargetResourceId() {
        return Optional.ofNullable(targetResourceId);
    }

    public TimeSlot getRequestedTimeSlot() {
        return requestedTimeSlot;
    }

    public Duration getDuration() {
        return requestedTimeSlot.toDuration();
    }

    public int getRequiredCapacity() {
        return requiredCapacity;
    }

    public RequestPriority getPriority() {
        return priority;
    }

    public String getPurpose() {
        return purpose;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    // --- Domain State Machine Transitions ---

    public void approve() {
        if (status.isTerminal()) {
            throw new InvalidRequestException(id, "Cannot approve a finalized request in status: " + status);
        }
        this.status = RequestStatus.APPROVED;
        this.statusReason = "Resource successfully allocated";
    }

    public void waitlist(String reason) {
        if (status.isTerminal()) {
            throw new InvalidRequestException(id, "Cannot waitlist a finalized request in status: " + status);
        }
        this.status = RequestStatus.WAITLISTED;
        this.statusReason = ValidationUtils.requireNonBlank(reason, "waitlist reason");
    }

    public void reject(String reason) {
        if (status.isTerminal()) {
            throw new InvalidRequestException(id, "Cannot reject a finalized request in status: " + status);
        }
        this.status = RequestStatus.REJECTED;
        this.statusReason = ValidationUtils.requireNonBlank(reason, "rejection reason");
    }

    public void cancel(String reason) {
        if (status == RequestStatus.COMPLETED) {
            throw new InvalidRequestException(id, "Cannot cancel an already completed request");
        }
        this.status = RequestStatus.CANCELLED;
        this.statusReason = ValidationUtils.requireNonBlank(reason, "cancellation reason");
    }

    public void complete() {
        if (status != RequestStatus.APPROVED) {
            throw new InvalidRequestException(id, "Only approved requests can transition to COMPLETED");
        }
        this.status = RequestStatus.COMPLETED;
    }

    public void resetToPending() {
        if (status != RequestStatus.CANCELLED) {
            this.status = RequestStatus.PENDING;
            this.statusReason = null;
        }
    }

    /**
     * Effective combined priority score combining Requester institutional priority and Request urgency.
     */
    public int calculateEffectivePriority() {
        return requester.getAllocationPriority() + (priority.getRank() * 10);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceRequest that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Request[%s: %s by %s, %s, ReqCap: %d, Status: %s]",
                id, resourceType.getDisplayName(), requester.getName(), requestedTimeSlot, requiredCapacity, status);
    }
}
