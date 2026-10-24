package com.campus.strategy;

import com.campus.model.AllocationRequest;
import com.campus.model.Resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * =========================================================================
 * DESIGN PATTERN: STRATEGY IMPLEMENTATION (Capacity-Optimized / Best-Fit)
 * =========================================================================
 * Minimizes unused seats and wasted campus spaces.
 * Selects the smallest viable resource that can comfortably satisfy the requested capacity.
 */
public class CapacityStrategy implements AllocationStrategy {

    @Override
    public String getName() {
        return "Capacity-Optimized (Best-Fit)";
    }

    @Override
    public String getDescription() {
        return "Minimizes wasted capacity by selecting the smallest suitable resource for each request.";
    }

    @Override
    public List<AllocationRequest> prioritizeRequests(List<AllocationRequest> requests) {
        List<AllocationRequest> sorted = new ArrayList<>(requests);
        // Larger groups evaluated first to secure scarce venues, then by priority
        sorted.sort(Comparator.comparingInt(AllocationRequest::getRequiredCapacity).reversed()
                .thenComparingInt(AllocationRequest::getCombinedPriorityScore).reversed());
        return sorted;
    }

    // [JAVA CONCEPT - STREAMS & OPTIONAL]: Best-Fit Heuristic
    @Override
    public Resource selectBestResource(AllocationRequest request, List<Resource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        int required = request.getRequiredCapacity();

        // Best-fit: find candidate where (capacity - required) is minimum and >= 0
        return candidates.stream()
                .filter(r -> r.canAccommodate(required))
                .min(Comparator.comparingInt(r -> (r.getCapacity() - required)))
                .orElse(candidates.get(0));
    }
}
