package com.campus.simulator.service.allocation;

import com.campus.simulator.util.ValidationUtils;

/**
 * Factory class implementing the Factory Method pattern for creating
 * AllocationStrategy instances based on strategy type.
 */
public final class AllocationStrategyFactory {

    private AllocationStrategyFactory() {
        // Prevent instantiation
    }

    /**
     * Creates an AllocationStrategy corresponding to the specified type.
     */
    public static AllocationStrategy createStrategy(AllocationStrategyType type) {
        ValidationUtils.requireNonNull(type, "strategyType");
        return switch (type) {
            case PRIORITY_FIRST -> new PriorityFirstAllocationStrategy();
            case FAIRNESS_AWARE -> new FairnessAwareAllocationStrategy();
            case CAPACITY_OPTIMIZED -> new CapacityOptimizedAllocationStrategy();
        };
    }

    /**
     * Default strategy when none is explicitly configured.
     */
    public static AllocationStrategy getDefaultStrategy() {
        return createStrategy(AllocationStrategyType.PRIORITY_FIRST);
    }
}
