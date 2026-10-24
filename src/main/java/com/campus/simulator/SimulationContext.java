package com.campus.simulator;

import com.campus.simulator.observer.DomainEventPublisher;
import com.campus.simulator.repository.AllocationRepository;
import com.campus.simulator.repository.InMemoryAllocationRepository;
import com.campus.simulator.repository.InMemoryRequesterRepository;
import com.campus.simulator.repository.InMemoryResourceRepository;
import com.campus.simulator.repository.InMemoryResourceRequestRepository;
import com.campus.simulator.repository.RequesterRepository;
import com.campus.simulator.repository.ResourceRepository;
import com.campus.simulator.repository.ResourceRequestRepository;
import com.campus.simulator.service.AllocationService;
import com.campus.simulator.service.ReportingService;
import com.campus.simulator.service.RequesterService;
import com.campus.simulator.service.ResourceRequestService;
import com.campus.simulator.service.ResourceService;
import com.campus.simulator.service.SampleDataSeeder;
import com.campus.simulator.service.SimulationService;

/**
 * Application context managing dependency injection, repository lifecycles, and services.
 * Implements the Singleton pattern for top-level application assembly while ensuring
 * all internal components communicate strictly via constructor injection and abstractions.
 */
public class SimulationContext {

    private static final SimulationContext INSTANCE = new SimulationContext();

    private final DomainEventPublisher eventPublisher;
    private final ResourceRepository resourceRepository;
    private final RequesterRepository requesterRepository;
    private final ResourceRequestRepository requestRepository;
    private final AllocationRepository allocationRepository;

    private final ResourceService resourceService;
    private final RequesterService requesterService;
    private final ResourceRequestService requestService;
    private final AllocationService allocationService;
    private final SimulationService simulationService;
    private final ReportingService reportingService;

    public SimulationContext() {
        this.eventPublisher = DomainEventPublisher.getInstance();

        this.resourceRepository = new InMemoryResourceRepository();
        this.requesterRepository = new InMemoryRequesterRepository();
        this.requestRepository = new InMemoryResourceRequestRepository();
        this.allocationRepository = new InMemoryAllocationRepository();

        this.resourceService = new ResourceService(resourceRepository, allocationRepository, eventPublisher);
        this.requesterService = new RequesterService(requesterRepository, eventPublisher);
        this.requestService = new ResourceRequestService(requestRepository, requesterRepository, eventPublisher);
        this.allocationService = new AllocationService(allocationRepository, resourceRepository, requestRepository, eventPublisher);
        this.simulationService = new SimulationService(allocationService, requestRepository, allocationRepository,
                resourceRepository, requesterRepository, eventPublisher);
        this.reportingService = new ReportingService(resourceRepository, requesterRepository, requestRepository, allocationRepository);

        // Populate baseline demo dataset
        SampleDataSeeder seeder = new SampleDataSeeder(resourceRepository, requesterRepository, requestRepository);
        seeder.seed();
    }

    public static SimulationContext getInstance() {
        return INSTANCE;
    }

    public DomainEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public ResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    public RequesterRepository getRequesterRepository() {
        return requesterRepository;
    }

    public ResourceRequestRepository getRequestRepository() {
        return requestRepository;
    }

    public AllocationRepository getAllocationRepository() {
        return allocationRepository;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public RequesterService getRequesterService() {
        return requesterService;
    }

    public ResourceRequestService getRequestService() {
        return requestService;
    }

    public AllocationService getAllocationService() {
        return allocationService;
    }

    public SimulationService getSimulationService() {
        return simulationService;
    }

    public ReportingService getReportingService() {
        return reportingService;
    }
}
