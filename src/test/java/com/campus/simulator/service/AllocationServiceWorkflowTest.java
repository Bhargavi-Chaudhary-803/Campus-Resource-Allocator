package com.campus.simulator.service;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.service.allocation.AllocationStrategyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Service Layer End-to-End Workflow & Event Orchestration Tests")
class AllocationServiceWorkflowTest {

    private SimulationContext context;
    private List<DomainEvent> capturedEvents;

    @BeforeEach
    void setUp() {
        context = new SimulationContext();
        capturedEvents = new ArrayList<>();
        context.getEventPublisher().registerListener(capturedEvents::add);
    }

    @Test
    @DisplayName("Should run complete batch simulation and generate comprehensive analytical reports")
    void testEndToEndSimulationAndReporting() {
        SimulationService simulationService = context.getSimulationService();
        ReportingService reportingService = context.getReportingService();

        // Initial state from SampleDataSeeder
        assertTrue(context.getResourceRepository().count() >= 12, "Must seed >= 12 resources");
        assertTrue(context.getRequesterRepository().count() >= 8, "Must seed >= 8 requesters");
        assertTrue(context.getRequestRepository().count() >= 15, "Must seed >= 15 requests");

        // Run batch simulation with Capacity Optimized strategy
        List<AllocationResult> results = simulationService.runSimulationWith(AllocationStrategyType.CAPACITY_OPTIMIZED);
        assertFalse(results.isEmpty());

        long approvedCount = results.stream().filter(AllocationResult::isApproved).count();
        long waitlistedCount = results.stream().filter(AllocationResult::isWaitlisted).count();
        long rejectedCount = results.stream().filter(AllocationResult::isRejected).count();

        assertTrue(approvedCount > 0, "Simulation must approve compatible requests");
        assertTrue(waitlistedCount > 0, "Simulation must waitlist intentionally conflicting requests");
        assertTrue(rejectedCount > 0, "Simulation must reject policy-violating requests (e.g. BSL-2 lab by student club)");

        // Verify ReportingService calculations
        ReportingService.SimulationReport report = reportingService.generateReport();
        assertNotNull(report);
        assertEquals(context.getResourceRepository().count(), report.totalResources());
        assertEquals(context.getRequestRepository().count(), report.totalRequests());
        assertTrue(report.approvalRatePercentage() > 0.0);
        assertTrue(report.resourceUtilizationPercentage() > 0.0);
        assertFalse(report.requestsByRequesterType().isEmpty());

        // Verify Domain Events were published
        assertTrue(capturedEvents.stream().anyMatch(e -> e.getEventName().equals("SimulationCompleted")));
    }

    @Test
    @DisplayName("Should release allocation and restore resource availability")
    void testReleaseAllocationWorkflow() {
        AllocationService allocationService = context.getAllocationService();
        ResourceService resourceService = context.getResourceService();
        RequesterService requesterService = context.getRequesterService();
        ResourceRequestService requestService = context.getRequestService();

        Resource room = resourceService.addResource(ResourceType.CLASSROOM, "Test Hall", "Bldg X", 50, Map.of());
        Requester dept = requesterService.addRequester(RequesterType.DEPARTMENT, "Dept X", "x@c.edu", PriorityTier.HIGH, Map.of());

        TimeSlot slot = TimeSlot.of(LocalDateTime.of(2026, 12, 1, 9, 0), 60);
        ResourceRequest req = requestService.submitRequest(dept.getId(), ResourceType.CLASSROOM, room.getId(),
                slot, 30, RequestPriority.NORMAL, "Special Event");

        AllocationResult result = allocationService.processRequest(req);
        assertTrue(result.isApproved());
        Allocation allocation = result.getAllocation().orElseThrow();

        // Release allocation early
        allocationService.releaseAllocation(allocation.getId(), "Session finished early");

        Allocation updated = allocationService.getAllocation(allocation.getId()).orElseThrow();
        assertEquals("Session finished early", updated.getStatusReason());
        assertFalse(updated.getStatus().isActive());
        assertTrue(capturedEvents.stream().anyMatch(e -> e.getEventName().equals("AllocationReleased")));
    }
}
