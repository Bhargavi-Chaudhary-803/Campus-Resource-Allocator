package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: ABSTRACTION & ENCAPSULATION
 * =========================================================================
 * Abstract base class representing university actors who submit resource requests.
 * Encapsulates identity, institutional standing, and historical allocation counts.
 */
public abstract class Requester {

    // [OOP - ENCAPSULATION]: Private fields protected from outside mutation
    private final String id;
    private String name;
    private String email;
    private final RequesterType type;
    private PriorityLevel priorityLevel;
    private int totalAllocationsReceived;

    public Requester(String id, String name, String email, RequesterType type, PriorityLevel priorityLevel) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Requester ID cannot be empty.");
        }
        this.id = id.trim();
        this.name = name != null ? name.trim() : "Unknown";
        this.email = email != null ? email.trim() : "no-email@campus.edu";
        this.type = type;
        this.priorityLevel = priorityLevel;
        this.totalAllocationsReceived = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim();
        }
    }

    public RequesterType getType() {
        return type;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public PriorityLevel getPriority() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getTotalAllocationsReceived() {
        return totalAllocationsReceived;
    }

    public int getAllocationCount() {
        return totalAllocationsReceived;
    }

    public void incrementAllocationCount() {
        this.totalAllocationsReceived++;
    }

    public void resetAllocationCount() {
        this.totalAllocationsReceived = 0;
    }

    /**
     * Calculates the combined numerical priority score (1 to 100).
     */
    public int calculateEffectiveScore() {
        return getBasePriorityScore() + (priorityLevel.getValue() * 10);
    }

    // =========================================================================
    // [OOP - ABSTRACTION & POLYMORPHISM]: Abstract methods
    // =========================================================================
    public abstract int getBasePriorityScore();

    public abstract boolean canBook(Resource resource);

    public abstract String getRoleDescription();

    @Override
    public String toString() {
        return String.format("%s (%s, %s)", name, type.getLabel(), priorityLevel.getLabel());
    }
}
