package com.campus.simulator.service;

import com.campus.simulator.exception.EntityNotFoundException;
import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.AllocationStatus;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.observer.AllocationApprovedEvent;
import com.campus.simulator.observer.AllocationReleasedEvent;
import com.campus.simulator.observer.DomainEventPublisher;
import com.campus.simulator.observer.RequestStatusChangedEvent;
import com.campus.simulator.repository.AllocationRepository;
import com.campus.simulator.repository.ResourceRepository;
import com.campus.simulator.repository.ResourceRequestRepository;
import com.campus.simulator.service.allocation.AllocationStrategy;
import com.campus.simulator.service.allocation.AllocationStrategyFactory;
import com.campus.simulator.service.allocation.AllocationStrategyType;
import com.campus.simulator.util.ValidationUtils;

import java.util.List;
import java.util.Optional;

/**
 * Core application service executing allocation logic, managing reservation lifecycle,
 * and decoupling the domain strategy from persistence.
 */
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceRequestRepository requestRepository;
    private final DomainEventPublisher eventPublisher;

    private AllocationStrategy currentStrategy;

    public AllocationService(AllocationRepository allocationRepository,
                             ResourceRepository resourceRepository,
                             ResourceRequestRepository requestRepository,
                             DomainEventPublisher eventPublisher) {
        this.allocationRepository = ValidationUtils.requireNonNull(allocationRepository, "allocationRepository");
        this.resourceRepository = ValidationUtils.requireNonNull(resourceRepository, "resourceRepository");
        this.requestRepository = ValidationUtils.requireNonNull(requestRepository, "requestRepository");
        this.eventPublisher = ValidationUtils.requireNonNull(eventPublisher, "eventPublisher");
        this.currentStrategy = AllocationStrategyFactory.getDefaultStrategy();
    }

    public AllocationStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    public void setStrategy(AllocationStrategy strategy) {
        this.currentStrategy = ValidationUtils.requireNonNull(strategy, "strategy");
    }

    public void setStrategy(AllocationStrategyType type) {
        setStrategy(AllocationStrategyFactory.createStrategy(type));
    }

    /**
     * Executes allocation for an individual request using the active strategy.
     */
    public AllocationResult processRequest(ResourceRequest request) {
        ValidationUtils.requireNonNull(request, "request");

        List<Resource> candidates = resourceRepository.findAll();
        List<Allocation> existing = allocationRepository.findAll();

        RequestStatus oldStatus = request.getStatus();
        AllocationResult result = currentStrategy.allocate(request, candidates, existing);

        if (result.isApproved() && result.getAllocation().isPresent()) {
            Allocation allocation = result.getAllocation().get();
            allocationRepository.save(allocation);
            requestRepository.save(request);

            eventPublisher.publish(new AllocationApprovedEvent(allocation));
            eventPublisher.publish(new RequestStatusChangedEvent(
                    request, oldStatus, RequestStatus.APPROVED, result.getExplanation()));
        } else if (result.isWaitlisted()) {
            request.waitlist(result.getExplanation());
            requestRepository.save(request);
            eventPublisher.publish(new RequestStatusChangedEvent(
                    request, oldStatus, RequestStatus.WAITLISTED, result.getExplanation()));
        } else if (result.isRejected()) {
            request.reject(result.getExplanation());
            requestRepository.save(request);
            eventPublisher.publish(new RequestStatusChangedEvent(
                    request, oldStatus, RequestStatus.REJECTED, result.getExplanation()));
        }

        return result;
    }

    /**
     * Releases an active allocation ahead of schedule.
     */
    public void releaseAllocation(String allocationId, String reason) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new EntityNotFoundException("Allocation", allocationId));

        allocation.release(reason);
        allocationRepository.save(allocation);

        eventPublisher.publish(new AllocationReleasedEvent(allocation));
    }

    /**
     * Cancels an allocation.
     */
    public void cancelAllocation(String allocationId, String reason) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new EntityNotFoundException("Allocation", allocationId));

        allocation.cancel(reason);
        allocationRepository.save(allocation);

        eventPublisher.publish(new AllocationReleasedEvent(allocation));
    }

    public List<Allocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    public List<Allocation> getActiveAllocations() {
        return allocationRepository.findByStatus(AllocationStatus.ACTIVE);
    }

    public Optional<Allocation> getAllocation(String id) {
        return allocationRepository.findById(id);
    }

    public long getTotalAllocationsCount() {
        return allocationRepository.count();
    }
}
