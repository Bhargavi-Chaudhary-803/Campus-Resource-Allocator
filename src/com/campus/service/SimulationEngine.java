package com.campus.service;

import com.campus.model.*;
import com.campus.strategy.AllocationStrategy;
import com.campus.strategy.PriorityStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * CORE ENGINE: ALLOCATION & SIMULATION ORCHESTRATOR
 * =========================================================================
 * Evaluates pending requests against campus resources using the configured
 * AllocationStrategy. Checks for:
 * 1. Double-booking prevention (resource & requester level)
 * 2. Institutional safety clearance policies
 * 3. Maintenance states
 * 4. Capacity constraints
 */
public class SimulationEngine {

    private final CampusDatabase db;
    private AllocationStrategy currentStrategy;

    public SimulationEngine() {
        this.db = CampusDatabase.getInstance();
        this.currentStrategy = new PriorityStrategy(); // Default strategy
    }

    public AllocationStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    public void setStrategy(AllocationStrategy strategy) {
        if (strategy != null) {
            this.currentStrategy = strategy;
        }
    }

    /**
     * Executes the simulation on all pending requests in the database.
     */
    public synchronized List<AllocationRequest> runSimulation() {
        // Reset previous run allocations to test the strategy cleanly
        db.clearAllocations();

        // 1. Get all pending requests and prioritize them using the active strategy
        List<AllocationRequest> pendingRequests = new ArrayList<>(
                db.getRequests().stream()
                        .filter(r -> r.getStatus() == RequestStatus.PENDING)
                        .toList()
        );

        // [DESIGN PATTERN: STRATEGY]: Request ordering delegated to strategy
        List<AllocationRequest> prioritized = currentStrategy.prioritizeRequests(pendingRequests);

        for (AllocationRequest request : prioritized) {
            processIndividualRequest(request);
        }

        return prioritized;
    }

    /**
     * Evaluates a single request against available campus resources.
     */
    public void processIndividualRequest(AllocationRequest request) {
        TimeSlot slot = request.getTimeSlot();
        Requester requester = request.getRequester();

        // Rule 1: Prevent requester from double-booking themselves for the same time slot
        boolean requesterAlreadyBusy = db.getAllocations().stream()
                .filter(Allocation::isActive)
                .filter(a -> a.getRequest().getRequester().equals(requester))
                .anyMatch(a -> a.conflictsWith(slot));

        if (requesterAlreadyBusy) {
            request.markWaitlisted("Requester already holds an active allocation for this time slot.");
            return;
        }

        // Rule 2: Filter candidate resources matching the requested type
        List<Resource> candidates = db.getResources().stream()
                .filter(r -> r.getType() == request.getRequestedType())
                .filter(r -> {
                    if (request.getSpecificResourceId() != null) {
                        return r.getId().equalsIgnoreCase(request.getSpecificResourceId());
                    }
                    return true;
                })
                .toList();

        if (candidates.isEmpty()) {
            request.markRejected("No registered resources found matching type " + request.getRequestedType().getDisplayName());
            return;
        }

        // Rule 3: Check institutional safety clearance policies (Polymorphic method)
        boolean hasPolicyClearance = candidates.stream().anyMatch(requester::canBook);
        if (!hasPolicyClearance) {
            request.markRejected("Policy Violation: Requester lacks mandatory safety clearance for this facility.");
            return;
        }

        // Rule 4: Identify fully viable candidates (Not in maintenance, sufficient capacity, free of schedule collisions)
        List<Resource> viableResources = new ArrayList<>();
        List<String> collisionReasons = new ArrayList<>();

        for (Resource candidate : candidates) {
            if (candidate.isUnderMaintenance()) {
                collisionReasons.add(candidate.getName() + " is currently under scheduled maintenance.");
                continue;
            }

            if (!candidate.canAccommodate(request.getRequiredCapacity())) {
                collisionReasons.add(candidate.getName() + " capacity (" + candidate.getCapacity() + ") is too small for requested " + request.getRequiredCapacity() + " seats.");
                continue;
            }

            if (!requester.canBook(candidate)) {
                collisionReasons.add("Requester lacks safety clearance for " + candidate.getName() + ".");
                continue;
            }

            // Check if another active allocation collides with this time slot
            boolean hasScheduleConflict = db.getAllocations().stream()
                    .filter(Allocation::isActive)
                    .filter(a -> a.getResource().equals(candidate))
                    .anyMatch(a -> a.conflictsWith(slot));

            if (hasScheduleConflict) {
                collisionReasons.add(candidate.getName() + " is already occupied during requested slot.");
                continue;
            }

            // If it passes all tests, it is a viable candidate!
            viableResources.add(candidate);
        }

        if (viableResources.isEmpty()) {
            String explanation = "Waitlisted: " + String.join(" | ", collisionReasons);
            request.markWaitlisted(explanation);
            return;
        }

        // [DESIGN PATTERN: STRATEGY]: Delegate candidate selection to active strategy
        Resource chosenResource = currentStrategy.selectBestResource(request, viableResources);

        if (chosenResource != null && chosenResource.book()) {
            Allocation allocation = new Allocation(
                    db.generateNextAllocationId(),
                    chosenResource,
                    request,
                    slot
            );
            db.addAllocation(allocation);
            request.markAllocated("Successfully allocated " + chosenResource.getName() + " (Capacity: " + chosenResource.getCapacity() + ") via " + currentStrategy.getName());
        } else {
            request.markWaitlisted("Resource was momentarily unavailable.");
        }
    }
}
