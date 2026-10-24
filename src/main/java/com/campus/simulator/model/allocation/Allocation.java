package com.campus.simulator.model.allocation;

import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an active, completed, or released allocation of a physical Resource
 * for a specific ResourceRequest during an approved TimeSlot.
 */
public class Allocation {

    private final String id;
    private final Resource resource;
    private final ResourceRequest request;
    private final TimeSlot approvedTimeSlot;
    private final LocalDateTime allocatedAt;

    private AllocationStatus status;
    private String statusReason;

    public Allocation(String id,
                      Resource resource,
                      ResourceRequest request,
                      TimeSlot approvedTimeSlot) {
        this(id, resource, request, approvedTimeSlot, LocalDateTime.now(), AllocationStatus.ACTIVE, null);
    }

    public Allocation(String id,
                      Resource resource,
                      ResourceRequest request,
                      TimeSlot approvedTimeSlot,
                      LocalDateTime allocatedAt,
                      AllocationStatus status,
                      String statusReason) {
        this.id = ValidationUtils.requireNonBlank(id, "id");
        this.resource = ValidationUtils.requireNonNull(resource, "resource");
        this.request = ValidationUtils.requireNonNull(request, "request");
        this.approvedTimeSlot = ValidationUtils.requireNonNull(approvedTimeSlot, "approvedTimeSlot");
        this.allocatedAt = ValidationUtils.requireNonNull(allocatedAt, "allocatedAt");
        this.status = ValidationUtils.requireNonNull(status, "status");
        this.statusReason = statusReason;
    }

    public String getId() {
        return id;
    }

    public Resource getResource() {
        return resource;
    }

    public ResourceRequest getRequest() {
        return request;
    }

    public TimeSlot getApprovedTimeSlot() {
        return approvedTimeSlot;
    }

    public LocalDateTime getAllocatedAt() {
        return allocatedAt;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    /**
     * Checks if this allocation is active and overlaps with the given time slot.
     */
    public boolean isActiveDuring(TimeSlot slot) {
        return status.isActive() && approvedTimeSlot.overlaps(slot);
    }

    /**
     * Releases this allocation early, making the resource immediately available for other bookings.
     */
    public void release(String reason) {
        if (status == AllocationStatus.COMPLETED) {
            throw new ValidationException("Cannot release an allocation that has already completed");
        }
        if (status == AllocationStatus.CANCELLED) {
            throw new ValidationException("Cannot release an allocation that has already been cancelled");
        }
        this.status = AllocationStatus.RELEASED;
        this.statusReason = reason != null ? reason : "Released early";
    }

    /**
     * Marks the reservation as normally fulfilled and concluded.
     */
    public void complete() {
        if (status != AllocationStatus.ACTIVE) {
            throw new ValidationException("Only active allocations can transition to COMPLETED");
        }
        this.status = AllocationStatus.COMPLETED;
        this.statusReason = "Fulfilled scheduled duration";
        this.request.complete();
    }

    /**
     * Cancels the allocation before or during execution.
     */
    public void cancel(String reason) {
        if (status == AllocationStatus.COMPLETED) {
            throw new ValidationException("Cannot cancel an already completed allocation");
        }
        this.status = AllocationStatus.CANCELLED;
        this.statusReason = ValidationUtils.requireNonBlank(reason, "cancel reason");
        this.request.cancel(reason);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Allocation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Allocation[%s: %s -> %s @ %s, Status: %s]",
                id, resource.getName(), request.getRequester().getName(), approvedTimeSlot, status);
    }
}
