package com.campus.simulator.model.allocation;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Value object reporting the evaluation outcome of an allocation attempt on a single ResourceRequest.
 * Provides transparent explanations, considered candidate resources, and conflict diagnostics.
 */
public class AllocationResult {

    private final ResourceRequest request;
    private final Allocation allocation;
    private final AllocationOutcome outcome;
    private final String explanation;
    private final List<Resource> consideredResources;
    private final List<String> conflictDetails;

    public AllocationResult(ResourceRequest request,
                            Allocation allocation,
                            AllocationOutcome outcome,
                            String explanation,
                            List<Resource> consideredResources,
                            List<String> conflictDetails) {
        this.request = ValidationUtils.requireNonNull(request, "request");
        this.allocation = allocation;
        this.outcome = ValidationUtils.requireNonNull(outcome, "outcome");
        this.explanation = ValidationUtils.requireNonBlank(explanation, "explanation");
        this.consideredResources = consideredResources == null
                ? List.of() : List.copyOf(consideredResources);
        this.conflictDetails = conflictDetails == null
                ? List.of() : List.copyOf(conflictDetails);
    }

    public ResourceRequest getRequest() {
        return request;
    }

    public Optional<Allocation> getAllocation() {
        return Optional.ofNullable(allocation);
    }

    public AllocationOutcome getOutcome() {
        return outcome;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<Resource> getConsideredResources() {
        return consideredResources;
    }

    public List<String> getConflictDetails() {
        return conflictDetails;
    }

    public boolean isApproved() {
        return outcome == AllocationOutcome.APPROVED;
    }

    public boolean isWaitlisted() {
        return outcome == AllocationOutcome.WAITLISTED;
    }

    public boolean isRejected() {
        return outcome == AllocationOutcome.REJECTED;
    }

    public static AllocationResult approved(ResourceRequest request, Allocation allocation, String explanation) {
        return new AllocationResult(
                request,
                allocation,
                AllocationOutcome.APPROVED,
                explanation,
                List.of(allocation.getResource()),
                List.of()
        );
    }

    public static AllocationResult waitlisted(ResourceRequest request,
                                             String explanation,
                                             List<Resource> considered,
                                             List<String> conflicts) {
        return new AllocationResult(
                request,
                null,
                AllocationOutcome.WAITLISTED,
                explanation,
                considered,
                conflicts
        );
    }

    public static AllocationResult rejected(ResourceRequest request,
                                            String explanation,
                                            List<String> conflicts) {
        return new AllocationResult(
                request,
                null,
                AllocationOutcome.REJECTED,
                explanation,
                List.of(),
                conflicts
        );
    }

    public static AllocationResultBuilder builder() {
        return new AllocationResultBuilder();
    }

    @Override
    public String toString() {
        return String.format("AllocationResult[%s: %s - %s]",
                request.getId(), outcome, explanation);
    }
}
