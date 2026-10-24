package com.campus.simulator.service.allocation;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceStatus;
import com.campus.simulator.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Skeletal base class for allocation strategies implementing standard domain validation,
 * collision detection, and requester policy enforcement.
 */
public abstract class AbstractAllocationStrategy implements AllocationStrategy {

    private final String name;
    private final String description;

    protected AbstractAllocationStrategy(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AllocationResult allocate(ResourceRequest request,
                                     List<Resource> candidateResources,
                                     List<Allocation> existingAllocations) {
        if (!request.getRequester().isActive()) {
            return AllocationResult.rejected(
                    request,
                    "Requester [" + request.getRequester().getName() + "] is inactive",
                    List.of("Requester deactivated by campus policy")
            );
        }

        // Rule: A requester cannot hold overlapping active allocations for the same time slot
        for (Allocation existing : existingAllocations) {
            if (existing.getStatus().isActive()
                    && existing.getRequest().getRequester().equals(request.getRequester())
                    && existing.isActiveDuring(request.getRequestedTimeSlot())) {
                return AllocationResult.waitlisted(
                        request,
                        "Requester already holds active allocation [" + existing.getId()
                                + "] for overlapping time slot " + existing.getApprovedTimeSlot().getFormatted(),
                        List.of(),
                        List.of("Concurrent requester schedule collision")
                );
            }
        }

        List<Resource> considered = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();
        List<Resource> viableResources = new ArrayList<>();

        for (Resource candidate : candidateResources) {
            // Match resource type
            if (candidate.getResourceType() != request.getResourceType()) {
                continue;
            }

            // Target resource check if requested specifically
            if (request.getTargetResourceId().isPresent()) {
                String targetId = request.getTargetResourceId().get();
                if (!candidate.getId().equalsIgnoreCase(targetId)) {
                    continue;
                }
            }

            considered.add(candidate);

            // Check operational status
            if (candidate.getStatus() != ResourceStatus.AVAILABLE) {
                conflicts.add(String.format("Resource [%s] is %s: %s",
                        candidate.getName(),
                        candidate.getStatus().getDisplayName(),
                        candidate.getStatusReason() != null ? candidate.getStatusReason() : "N/A"));
                continue;
            }

            // Check capacity
            if (!candidate.canAccommodate(request.getRequiredCapacity())) {
                conflicts.add(String.format("Resource [%s] capacity (%d) insufficient for requested %d",
                        candidate.getName(), candidate.getCapacity(), request.getRequiredCapacity()));
                continue;
            }

            // Check requester permissions / safety constraints
            if (!request.getRequester().canRequestResource(candidate)) {
                conflicts.add(String.format("Requester [%s] lacks authorization/safety clearance for [%s]",
                        request.getRequester().getName(), candidate.getName()));
                continue;
            }

            // Check active allocation collisions on this resource
            boolean hasCollision = false;
            for (Allocation existing : existingAllocations) {
                if (existing.getStatus().isActive()
                        && existing.getResource().equals(candidate)
                        && existing.isActiveDuring(request.getRequestedTimeSlot())) {
                    conflicts.add(String.format("Resource [%s] is already reserved during %s by [%s]",
                            candidate.getName(),
                            existing.getApprovedTimeSlot().getFormatted(),
                            existing.getRequest().getRequester().getName()));
                    hasCollision = true;
                    break;
                }
            }

            if (!hasCollision) {
                viableResources.add(candidate);
            }
        }

        if (viableResources.isEmpty()) {
            if (considered.isEmpty()) {
                return AllocationResult.rejected(
                        request,
                        "No registered campus resources match type " + request.getResourceType().getDisplayName(),
                        List.of("No catalog assets found")
                );
            }

            // Check if requester lacks authorization or safety clearance for all evaluated candidates
            boolean policyProhibited = considered.stream()
                    .noneMatch(c -> request.getRequester().canRequestResource(c));

            if (policyProhibited) {
                return AllocationResult.rejected(
                        request,
                        "Request rejected: Requester [" + request.getRequester().getName()
                                + "] lacks safety clearance or authorization for "
                                + request.getResourceType().getDisplayName() + " under campus policy",
                        conflicts
                );
            }

            return AllocationResult.waitlisted(
                    request,
                    "No viable " + request.getResourceType().getDisplayName() + " available without conflict",
                    considered,
                    conflicts
            );
        }

        // Delegate resource selection to concrete strategy algorithm
        Resource chosen = selectResource(viableResources, request, existingAllocations);

        Allocation allocation = new Allocation(
                IdGenerator.nextId("ALC"),
                chosen,
                request,
                request.getRequestedTimeSlot()
        );

        request.approve();

        String explanation = String.format("Allocated %s [%s] (Cap: %d) via %s",
                chosen.getResourceType().getDisplayName(),
                chosen.getName(),
                chosen.getCapacity(),
                getName());

        return AllocationResult.approved(request, allocation, explanation);
    }

    /**
     * Algorithmic hook for selecting the single best resource from the viable candidates.
     */
    protected abstract Resource selectResource(List<Resource> viableResources,
                                              ResourceRequest request,
                                              List<Allocation> existingAllocations);
}
