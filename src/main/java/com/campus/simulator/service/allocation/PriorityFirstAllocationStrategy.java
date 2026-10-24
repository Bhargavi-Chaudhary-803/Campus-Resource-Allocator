package com.campus.simulator.service.allocation;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;

import java.util.Comparator;
import java.util.List;

/**
 * Concrete AllocationStrategy that prioritizes the highest institutional ranking
 * and request urgency, breaking ties by chronological submission time (FIFO).
 */
public class PriorityFirstAllocationStrategy extends AbstractAllocationStrategy {

    public PriorityFirstAllocationStrategy() {
        super(AllocationStrategyType.PRIORITY_FIRST.getDisplayName(),
                AllocationStrategyType.PRIORITY_FIRST.getDescription());
    }

    @Override
    public Comparator<ResourceRequest> getRequestComparator(List<Allocation> existingAllocations) {
        return Comparator
                .comparingInt(ResourceRequest::calculateEffectivePriority).reversed()
                .thenComparing(ResourceRequest::getCreatedAt)
                .thenComparing(ResourceRequest::getId);
    }

    @Override
    protected Resource selectResource(List<Resource> viableResources,
                                      ResourceRequest request,
                                      List<Allocation> existingAllocations) {
        // Deterministic selection: pick first matching candidate sorted by ID
        return viableResources.stream()
                .min(Comparator.comparing(Resource::getId))
                .orElse(viableResources.get(0));
    }
}
