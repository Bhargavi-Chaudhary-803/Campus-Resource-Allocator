package com.campus.simulator.model.resource;

import com.campus.simulator.exception.ResourceUnavailableException;
import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class representing a managed campus asset.
 * Demonstrates encapsulation, abstraction, and capability segregation.
 */
public abstract class Resource implements Bookable, Maintainable, CapacityProvider {

    private final String id;
    private final String name;
    private final String location;
    private final int capacity;
    private final ResourceType resourceType;
    private final LocalDateTime createdAt;

    private ResourceStatus status;
    private String statusReason;

    protected Resource(String id,
                       String name,
                       String location,
                       int capacity,
                       ResourceType resourceType) {
        this(id, name, location, capacity, resourceType, ResourceStatus.AVAILABLE, null, LocalDateTime.now());
    }

    protected Resource(String id,
                       String name,
                       String location,
                       int capacity,
                       ResourceType resourceType,
                       ResourceStatus status,
                       String statusReason,
                       LocalDateTime createdAt) {
        this.id = ValidationUtils.requireNonBlank(id, "id");
        this.name = ValidationUtils.requireNonBlank(name, "name");
        this.location = ValidationUtils.requireNonBlank(location, "location");
        this.capacity = ValidationUtils.requirePositive(capacity, "capacity");
        this.resourceType = ValidationUtils.requireNonNull(resourceType, "resourceType");
        this.status = ValidationUtils.requireNonNull(status, "status");
        this.statusReason = statusReason;
        this.createdAt = ValidationUtils.requireNonNull(createdAt, "createdAt");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public ResourceStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    @Override
    public boolean isBookable() {
        return status == ResourceStatus.AVAILABLE;
    }

    @Override
    public void markUnderMaintenance(String reason) {
        if (this.status == ResourceStatus.RETIRED) {
            throw new ResourceUnavailableException(id, "Cannot place a retired resource into maintenance");
        }
        this.status = ResourceStatus.MAINTENANCE;
        this.statusReason = ValidationUtils.requireNonBlank(reason, "maintenance reason");
    }

    @Override
    public void markAvailable() {
        if (this.status == ResourceStatus.RETIRED) {
            throw new ResourceUnavailableException(id, "Cannot restore a retired resource to available");
        }
        this.status = ResourceStatus.AVAILABLE;
        this.statusReason = null;
    }

    @Override
    public void retire(String reason) {
        this.status = ResourceStatus.RETIRED;
        this.statusReason = ValidationUtils.requireNonBlank(reason, "retirement reason");
    }

    /**
     * Polymorphic method providing human-readable summary of type-specific features.
     */
    public abstract String getSpecificDetailsSummary();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, Cap: %d, Status: %s)",
                id, name, resourceType.getDisplayName(), capacity, status);
    }
}
