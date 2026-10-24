package com.campus.simulator.service.allocation;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete AllocationStrategy that balances institutional priority with equitable fairness.
 * Tracks allocation count per requester, applying a progressive fairness penalty to requesters
 * who have already secured significant resources.
 */
public class FairnessAwareAllocationStrategy extends AbstractAllocationStrategy {

    public FairnessAwareAllocationStrategy() {
        super(AllocationStrategyType.FAIRNESS_AWARE.getDisplayName(),
                AllocationStrategyType.FAIRNESS_AWARE.getDescription());
    }

    @Override
    public Comparator<ResourceRequest> getRequestComparator(List<Allocation> existingAllocations) {
        // Count active allocations held by each requester
        Map<String, Long> allocationsByRequester = existingAllocations.stream()
                .filter(a -> a.getStatus().isActive())
                .collect(Collectors.groupingBy(
                        a -> a.getRequest().getRequester().getId(),
                        Collectors.counting()
                ));

        return (req1, req2) -> {
            double score1 = calculateFairnessScore(req1, allocationsByRequester.getOrDefault(req1.getRequester().getId(), 0L));
            double score2 = calculateFairnessScore(req2, allocationsByRequester.getOrDefault(req2.getRequester().getId(), 0L));

            int cmp = Double.compare(score2, score1); // Higher fair score first
            if (cmp != 0) {
                return cmp;
            }
            return req1.getCreatedAt().compareTo(req2.getCreatedAt());
        };
    }

    private double calculateFairnessScore(ResourceRequest request, long activeAllocations) {
        int priority = request.calculateEffectivePriority();
        double weight = request.getRequester().calculateRequestWeight();
        // Penalize priority progressively based on existing allocation load
        return priority / (1.0 + (activeAllocations * weight));
    }

    @Override
    protected Resource selectResource(List<Resource> viableResources,
                                      ResourceRequest request,
                                      List<Allocation> existingAllocations) {
        // Count existing reservations per resource to balance resource wear/load
        Map<String, Long> resourceUsageCount = existingAllocations.stream()
                .filter(a -> a.getStatus().isActive())
                .collect(Collectors.groupingBy(
                        a -> a.getResource().getId(),
                        Collectors.counting()
                ));

        return viableResources.stream()
                .min(Comparator
                        .comparingLong((Resource r) -> resourceUsageCount.getOrDefault(r.getId(), 0L))
                        .thenComparing(Resource::getId))
                .orElse(viableResources.get(0));
    }
}
