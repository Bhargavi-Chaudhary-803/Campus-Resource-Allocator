package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLE: COMPOSITION
 * =========================================================================
 * AllocationRequest composes a Requester, a TimeSlot, and required parameters
 * into a cohesive scheduling submission with a distinct state lifecycle.
 */
public class AllocationRequest {

    // [OOP - COMPOSITION]: Has-a relationships with other domain models
    private final String requestId;
    private final Requester requester;
    private final ResourceType requestedType;
    private final String specificResourceId; // optional (null means any compatible resource)
    private final TimeSlot timeSlot;
    private final int requiredCapacity;
    private final PriorityLevel requestPriority;
    private final String purpose;

    private RequestStatus status;
    private String decisionExplanation;

    public AllocationRequest(String requestId, Requester requester, ResourceType requestedType,
                             String specificResourceId, TimeSlot timeSlot, int requiredCapacity,
                             PriorityLevel requestPriority, String purpose) {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be null or empty.");
        }
        if (requester == null) {
            throw new IllegalArgumentException("Requester cannot be null.");
        }
        if (timeSlot == null) {
            throw new IllegalArgumentException("TimeSlot cannot be null.");
        }
        if (requiredCapacity <= 0) {
            throw new IllegalArgumentException("Required capacity must be > 0.");
        }
        this.requestId = requestId.trim();
        this.requester = requester;
        this.requestedType = requestedType;
        this.specificResourceId = (specificResourceId != null && !specificResourceId.isBlank())
                ? specificResourceId.trim() : null;
        this.timeSlot = timeSlot;
        this.requiredCapacity = requiredCapacity;
        this.requestPriority = requestPriority;
        this.purpose = purpose != null ? purpose.trim() : "Campus Event";
        this.status = RequestStatus.PENDING;
        this.decisionExplanation = "Pending Simulation";
    }

    public String getRequestId() {
        return requestId;
    }

    public Requester getRequester() {
        return requester;
    }

    public ResourceType getRequestedType() {
        return requestedType;
    }

    public String getSpecificResourceId() {
        return specificResourceId;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getRequiredCapacity() {
        return requiredCapacity;
    }

    public PriorityLevel getRequestPriority() {
        return requestPriority;
    }

    public String getPurpose() {
        return purpose;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getDecisionExplanation() {
        return decisionExplanation;
    }

    // State transitions
    public void markAllocated(String explanation) {
        this.status = RequestStatus.ALLOCATED;
        this.decisionExplanation = explanation;
        this.requester.incrementAllocationCount();
    }

    public void markWaitlisted(String reason) {
        this.status = RequestStatus.WAITLISTED;
        this.decisionExplanation = reason;
    }

    public void markRejected(String reason) {
        this.status = RequestStatus.REJECTED;
        this.decisionExplanation = reason;
    }

    public void markCancelled(String reason) {
        this.status = RequestStatus.CANCELLED;
        this.decisionExplanation = reason;
    }

    public void resetToPending() {
        if (this.status != RequestStatus.CANCELLED) {
            this.status = RequestStatus.PENDING;
            this.decisionExplanation = "Pending Simulation";
        }
    }

    /**
     * Calculates the overall priority score by combining the Requester's institutional standing
     * with the urgency of this specific request.
     */
    public int getCombinedPriorityScore() {
        return requester.calculateEffectiveScore() + (requestPriority.getValue() * 15);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s for %s (%s)",
                requestId, requestedType.getDisplayName(), requester.getName(), timeSlot, status);
    }
}
