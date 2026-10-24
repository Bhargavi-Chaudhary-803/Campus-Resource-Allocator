package com.campus.simulator.service;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.observer.DomainEventPublisher;
import com.campus.simulator.observer.SimulationCompletedEvent;
import com.campus.simulator.observer.SimulationResetEvent;
import com.campus.simulator.repository.AllocationRepository;
import com.campus.simulator.repository.RequesterRepository;
import com.campus.simulator.repository.ResourceRepository;
import com.campus.simulator.repository.ResourceRequestRepository;
import com.campus.simulator.service.allocation.AllocationStrategy;
import com.campus.simulator.service.allocation.AllocationStrategyType;
import com.campus.simulator.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Orchestrator service coordinating multi-request simulation runs, strategy comparisons,
 * and test data re-seeding.
 */
public class SimulationService {

    private final AllocationService allocationService;
    private final ResourceRequestRepository requestRepository;
    private final AllocationRepository allocationRepository;
    private final ResourceRepository resourceRepository;
    private final RequesterRepository requesterRepository;
    private final DomainEventPublisher eventPublisher;

    public SimulationService(AllocationService allocationService,
                             ResourceRequestRepository requestRepository,
                             AllocationRepository allocationRepository,
                             ResourceRepository resourceRepository,
                             RequesterRepository requesterRepository,
                             DomainEventPublisher eventPublisher) {
        this.allocationService = ValidationUtils.requireNonNull(allocationService, "allocationService");
        this.requestRepository = ValidationUtils.requireNonNull(requestRepository, "requestRepository");
        this.allocationRepository = ValidationUtils.requireNonNull(allocationRepository, "allocationRepository");
        this.resourceRepository = ValidationUtils.requireNonNull(resourceRepository, "resourceRepository");
        this.requesterRepository = ValidationUtils.requireNonNull(requesterRepository, "requesterRepository");
        this.eventPublisher = ValidationUtils.requireNonNull(eventPublisher, "eventPublisher");
    }

    /**
     * Executes the full simulation run on all pending requests using the currently selected strategy.
     */
    public List<AllocationResult> runSimulation() {
        AllocationStrategy strategy = allocationService.getCurrentStrategy();
        List<Allocation> existing = allocationRepository.findAll();

        // Retrieve all pending requests
        List<ResourceRequest> pending = new ArrayList<>(requestRepository.findByStatus(RequestStatus.PENDING));

        // Order requests according to strategy's custom comparator
        Comparator<ResourceRequest> comparator = strategy.getRequestComparator(existing);
        pending.sort(comparator);

        List<AllocationResult> results = new ArrayList<>();
        for (ResourceRequest request : pending) {
            AllocationResult result = allocationService.processRequest(request);
            results.add(result);
        }

        eventPublisher.publish(new SimulationCompletedEvent(strategy.getName(), results));
        return results;
    }

    /**
     * Re-runs the simulation with a specified strategy by first resetting non-terminal requests.
     */
    public List<AllocationResult> runSimulationWith(AllocationStrategyType strategyType) {
        allocationService.setStrategy(strategyType);
        resetAllocationsToPending();
        return runSimulation();
    }

    /**
     * Resets active allocations and returns requests back to PENDING so another strategy can be evaluated.
     */
    public void resetAllocationsToPending() {
        allocationRepository.clear();
        for (ResourceRequest req : requestRepository.findAll()) {
            if (req.getStatus() != RequestStatus.CANCELLED) {
                req.resetToPending();
                requestRepository.save(req);
            }
        }
    }

    /**
     * Resets the entire environment back to initial seed data state.
     */
    public void resetSimulationWithSeedData() {
        allocationRepository.clear();
        requestRepository.clear();
        resourceRepository.clear();
        requesterRepository.clear();

        SampleDataSeeder seeder = new SampleDataSeeder(resourceRepository, requesterRepository, requestRepository);
        seeder.seed();

        eventPublisher.publish(new SimulationResetEvent());
    }
}
