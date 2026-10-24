package com.campus.simulator.model.requester;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.util.ValidationUtils;

import java.util.Objects;

/**
 * Abstract base class representing any campus entity capable of requesting resource allocations.
 * Demonstrates inheritance, encapsulation, and polymorphic priority determination.
 */
public abstract class Requester {

    private final String id;
    private final String name;
    private final String contactEmail;
    private final RequesterType requesterType;

    private PriorityTier priorityTier;
    private boolean active;

    protected Requester(String id,
                        String name,
                        String contactEmail,
                        RequesterType requesterType,
                        PriorityTier priorityTier) {
        this(id, name, contactEmail, requesterType, priorityTier, true);
    }

    protected Requester(String id,
                        String name,
                        String contactEmail,
                        RequesterType requesterType,
                        PriorityTier priorityTier,
                        boolean active) {
        this.id = ValidationUtils.requireNonBlank(id, "id");
        this.name = ValidationUtils.requireNonBlank(name, "name");
        this.contactEmail = ValidationUtils.requireValidEmail(contactEmail, "contactEmail");
        this.requesterType = ValidationUtils.requireNonNull(requesterType, "requesterType");
        this.priorityTier = ValidationUtils.requireNonNull(priorityTier, "priorityTier");
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public RequesterType getRequesterType() {
        return requesterType;
    }

    public PriorityTier getPriorityTier() {
        return priorityTier;
    }

    public void setPriorityTier(PriorityTier priorityTier) {
        this.priorityTier = ValidationUtils.requireNonNull(priorityTier, "priorityTier");
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    /**
     * Polymorphic method computing base allocation score (1 to 100).
     */
    public abstract int getAllocationPriority();

    /**
     * Polymorphic method providing descriptive narrative of this requester.
     */
    public abstract String getDescription();

    /**
     * Polymorphic rule verifying whether this requester is allowed to book the given resource.
     * E.g., student clubs cannot book high-hazard laboratories without faculty sponsorship.
     */
    public abstract boolean canRequestResource(Resource resource);

    /**
     * Weight multiplier used in fairness calculations. Higher weight requires greater justification.
     */
    public abstract double calculateRequestWeight();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Requester other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %s, Active: %s)",
                id, name, requesterType.getDisplayName(), priorityTier, active);
    }
}
