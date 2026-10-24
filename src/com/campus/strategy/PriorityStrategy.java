package com.campus.strategy;

import com.campus.model.AllocationRequest;
import com.campus.model.Resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * =========================================================================
 * DESIGN PATTERN: STRATEGY IMPLEMENTATION (Priority-First)
 * =========================================================================
 * Prioritizes requests strictly based on the requester's institutional standing
 * and the urgency of the request.
 */
public class PriorityStrategy implements AllocationStrategy {

    @Override
    public String getName() {
        return "Priority-First Strategy";
    }

    @Override
    public String getDescription() {
        return "Allocates strictly by institutional ranking (Depts > Faculty > Clubs) and request urgency.";
    }

    // [JAVA CONCEPT - STREAMS & COMPARATORS]: Sorting requests by combined priority score
    @Override
    public List<AllocationRequest> prioritizeRequests(List<AllocationRequest> requests) {
        List<AllocationRequest> sorted = new ArrayList<>(requests);
        sorted.sort(Comparator.comparingInt(AllocationRequest::getCombinedPriorityScore).reversed());
        return sorted;
    }

    @Override
    public Resource selectBestResource(AllocationRequest request, List<Resource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        // Picks the first available candidate
        return candidates.get(0);
    }
}
