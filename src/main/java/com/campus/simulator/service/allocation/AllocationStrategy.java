package com.campus.simulator.service.allocation;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;

import java.util.Comparator;
import java.util.List;

/**
 * Strategy interface implementing the Strategy Pattern for resource allocation algorithms.
 * Demonstrates Open/Closed principle and Dependency Inversion.
 */
public interface AllocationStrategy {

    /**
     * Attempts to allocate a suitable Resource to fulfill the given ResourceRequest,
     * considering existing allocations and candidate resources.
     *
     * @param request             the allocation request to process
     * @param candidateResources  all potential resources in inventory
     * @param existingAllocations current historical/active allocations
     * @return the diagnostic AllocationResult (Approved, Waitlisted, or Rejected)
     */
    AllocationResult allocate(ResourceRequest request,
                              List<Resource> candidateResources,
                              List<Allocation> existingAllocations);

    /**
     * Returns a comparator used to order pending requests before batch allocation.
     * Different strategies may order incoming requests differently (e.g., priority vs fairness).
     */
    Comparator<ResourceRequest> getRequestComparator(List<Allocation> existingAllocations);

    /**
     * Human-readable name of this strategy.
     */
    String getName();

    /**
     * Description explaining how this strategy makes allocation decisions.
     */
    String getDescription();
}
