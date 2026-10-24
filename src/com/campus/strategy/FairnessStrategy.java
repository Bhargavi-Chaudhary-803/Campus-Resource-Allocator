package com.campus.strategy;

import com.campus.model.AllocationRequest;
import com.campus.model.Resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * =========================================================================
 * DESIGN PATTERN: STRATEGY IMPLEMENTATION (Fairness-Aware)
 * =========================================================================
 * Prevents heavy requesters (e.g. large departments) from monopolizing campus assets.
 * Applies a progressive penalty based on how many allocations the requester has already received.
 */
public class FairnessStrategy implements AllocationStrategy {

    @Override
    public String getName() {
        return "Fairness-Aware Strategy";
    }

    @Override
    public String getDescription() {
        return "Balances priority with equity; penalizes requesters holding multiple prior allocations.";
    }

    // [JAVA CONCEPT - LAMBDAS & COMPARATORS]: Custom multi-factor fairness score
    @Override
    public List<AllocationRequest> prioritizeRequests(List<AllocationRequest> requests) {
        List<AllocationRequest> sorted = new ArrayList<>(requests);
        sorted.sort((r1, r2) -> {
            double score1 = calculateFairnessScore(r1);
            double score2 = calculateFairnessScore(r2);
            return Double.compare(score2, score1); // Higher score first
        });
        return sorted;
    }

    private double calculateFairnessScore(AllocationRequest request) {
        int basePriority = request.getCombinedPriorityScore();
        int allocationsAlreadyHeld = request.getRequester().getTotalAllocationsReceived();
        // The more allocations already held, the lower the adjusted score!
        return basePriority / (1.0 + (allocationsAlreadyHeld * 1.5));
    }

    @Override
    public Resource selectBestResource(AllocationRequest request, List<Resource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0);
    }
}
