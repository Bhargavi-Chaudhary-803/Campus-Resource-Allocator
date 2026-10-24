package com.campus.simulator.service.allocation;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;

import java.util.Comparator;
import java.util.List;

/**
 * Concrete AllocationStrategy that minimizes unused seats and space waste (Best-Fit).
 * Always selects the smallest suitable viable resource that can accommodate the requested capacity.
 */
public class CapacityOptimizedAllocationStrategy extends AbstractAllocationStrategy {

    public CapacityOptimizedAllocationStrategy() {
        super(AllocationStrategyType.CAPACITY_OPTIMIZED.getDisplayName(),
                AllocationStrategyType.CAPACITY_OPTIMIZED.getDescription());
    }

    @Override
    public Comparator<ResourceRequest> getRequestComparator(List<Allocation> existingAllocations) {
        // Larger capacity requirements evaluated first to secure scarce large venues,
        // then prioritized by effective urgency.
        return Comparator
                .comparingInt(ResourceRequest::getRequiredCapacity).reversed()
                .thenComparingInt(ResourceRequest::calculateEffectivePriority).reversed()
                .thenComparing(ResourceRequest::getCreatedAt);
    }

    @Override
    protected Resource selectResource(List<Resource> viableResources,
                                      ResourceRequest request,
                                      List<Allocation> existingAllocations) {
        int required = request.getRequiredCapacity();

        // Best-fit: Minimize (capacity - required) >= 0
        return viableResources.stream()
                .min(Comparator
                        .comparingInt((Resource r) -> r.getCapacity() - required)
                        .thenComparing(Resource::getId))
                .orElse(viableResources.get(0));
    }
}
