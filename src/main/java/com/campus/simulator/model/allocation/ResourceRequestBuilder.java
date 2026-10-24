package com.campus.simulator.model.allocation;

import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;

import java.time.LocalDateTime;

/**
 * Builder class implementing the Builder Pattern for fluent and readable
 * instantiation of ResourceRequest objects.
 */
public class ResourceRequestBuilder {

    private String id;
    private Requester requester;
    private ResourceType resourceType;
    private String targetResourceId;
    private TimeSlot requestedTimeSlot;
    private int requiredCapacity = 1;
    private RequestPriority priority = RequestPriority.NORMAL;
    private String purpose = "Campus Activity";
    private RequestStatus status = RequestStatus.PENDING;
    private String statusReason;
    private LocalDateTime createdAt = LocalDateTime.now();

    public ResourceRequestBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ResourceRequestBuilder requester(Requester requester) {
        this.requester = requester;
        return this;
    }

    public ResourceRequestBuilder resourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public ResourceRequestBuilder targetResourceId(String targetResourceId) {
        this.targetResourceId = targetResourceId;
        return this;
    }

    public ResourceRequestBuilder requestedTimeSlot(TimeSlot requestedTimeSlot) {
        this.requestedTimeSlot = requestedTimeSlot;
        return this;
    }

    public ResourceRequestBuilder requiredCapacity(int requiredCapacity) {
        this.requiredCapacity = requiredCapacity;
        return this;
    }

    public ResourceRequestBuilder priority(RequestPriority priority) {
        this.priority = priority;
        return this;
    }

    public ResourceRequestBuilder purpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public ResourceRequestBuilder status(RequestStatus status) {
        this.status = status;
        return this;
    }

    public ResourceRequestBuilder statusReason(String statusReason) {
        this.statusReason = statusReason;
        return this;
    }

    public ResourceRequestBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ResourceRequest build() {
        return new ResourceRequest(
                id,
                requester,
                resourceType,
                targetResourceId,
                requestedTimeSlot,
                requiredCapacity,
                priority,
                purpose,
                status,
                statusReason,
                createdAt
        );
    }
}
