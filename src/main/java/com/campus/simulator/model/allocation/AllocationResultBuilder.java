package com.campus.simulator.model.allocation;

import com.campus.simulator.model.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for AllocationResult instances implementing the Builder pattern.
 */
public class AllocationResultBuilder {

    private ResourceRequest request;
    private Allocation allocation;
    private AllocationOutcome outcome = AllocationOutcome.WAITLISTED;
    private String explanation = "Evaluation pending";
    private final List<Resource> consideredResources = new ArrayList<>();
    private final List<String> conflictDetails = new ArrayList<>();

    public AllocationResultBuilder request(ResourceRequest request) {
        this.request = request;
        return this;
    }

    public AllocationResultBuilder allocation(Allocation allocation) {
        this.allocation = allocation;
        return this;
    }

    public AllocationResultBuilder outcome(AllocationOutcome outcome) {
        this.outcome = outcome;
        return this;
    }

    public AllocationResultBuilder explanation(String explanation) {
        this.explanation = explanation;
        return this;
    }

    public AllocationResultBuilder addConsideredResource(Resource resource) {
        if (resource != null) {
            this.consideredResources.add(resource);
        }
        return this;
    }

    public AllocationResultBuilder consideredResources(List<Resource> resources) {
        this.consideredResources.clear();
        if (resources != null) {
            this.consideredResources.addAll(resources);
        }
        return this;
    }

    public AllocationResultBuilder addConflictDetail(String conflict) {
        if (conflict != null && !conflict.isBlank()) {
            this.conflictDetails.add(conflict);
        }
        return this;
    }

    public AllocationResultBuilder conflictDetails(List<String> conflicts) {
        this.conflictDetails.clear();
        if (conflicts != null) {
            this.conflictDetails.addAll(conflicts);
        }
        return this;
    }

    public AllocationResult build() {
        return new AllocationResult(
                request,
                allocation,
                outcome,
                explanation,
                consideredResources,
                conflictDetails
        );
    }
}
