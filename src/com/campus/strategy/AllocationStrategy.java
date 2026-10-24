package com.campus.strategy;

import com.campus.model.AllocationRequest;
import com.campus.model.Resource;

import java.util.List;

/**
 * =========================================================================
 * DESIGN PATTERN: STRATEGY PATTERN
 * =========================================================================
 * Defines the family of swappable allocation algorithms.
 * Allows the simulator to dynamically switch between Priority-First,
 * Fairness-Aware, and Capacity-Optimized strategies at runtime.
 */
public interface AllocationStrategy {

    /**
     * @return Display name of the strategy.
     */
    String getName();

    /**
     * @return Description of how this strategy prioritizes and chooses resources.
     */
    String getDescription();

    /**
     * Orders a list of pending requests according to the strategy's priority formula.
     */
    List<AllocationRequest> prioritizeRequests(List<AllocationRequest> requests);

    /**
     * Chooses the optimal resource among viable candidate resources for a specific request.
     */
    Resource selectBestResource(AllocationRequest request, List<Resource> candidates);
}
