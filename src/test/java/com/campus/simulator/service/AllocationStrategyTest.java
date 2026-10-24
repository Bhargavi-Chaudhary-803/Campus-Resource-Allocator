package com.campus.simulator.service;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.allocation.ResourceRequestBuilder;
import com.campus.simulator.model.requester.Department;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.StudentClub;
import com.campus.simulator.model.resource.Classroom;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.service.allocation.CapacityOptimizedAllocationStrategy;
import com.campus.simulator.service.allocation.FairnessAwareAllocationStrategy;
import com.campus.simulator.service.allocation.PriorityFirstAllocationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Allocation Strategy Comparison Tests")
class AllocationStrategyTest {

    @Test
    @DisplayName("Should prove CapacityOptimized chooses the best-fit smallest suitable resource while conserving large venues")
    void testCapacityOptimizedVsStandard() {
        CapacityOptimizedAllocationStrategy capacityStrategy = new CapacityOptimizedAllocationStrategy();
        PriorityFirstAllocationStrategy priorityStrategy = new PriorityFirstAllocationStrategy();

        // Two viable classrooms: one massive auditorium (Cap 200) and one cozy seminar room (Cap 25)
        Classroom auditorium = new Classroom("RES-1", "Mega Auditorium", "Quad", 200, "1", true, true);
        Classroom seminarRoom = new Classroom("RES-2", "Cozy Seminar", "East Hall", 25, "2", true, true);

        // Candidate list where auditorium appears first
        List<Resource> candidates = List.of(auditorium, seminarRoom);

        Department dept = new Department("REQ-1", "Math", "m@c.edu", PriorityTier.HIGH, "M", "Lead");
        TimeSlot slot = TimeSlot.of(LocalDateTime.of(2026, 11, 10, 14, 0), 120);

        // Request requires only 20 seats
        ResourceRequest request = new ResourceRequestBuilder()
                .id("RQST-10")
                .requester(dept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slot)
                .requiredCapacity(20)
                .build();

        // 1. CapacityOptimized selects the 25-seat seminar room (best fit)
        AllocationResult capacityResult = capacityStrategy.allocate(request, candidates, List.of());
        assertTrue(capacityResult.isApproved());
        assertEquals("RES-2", capacityResult.getAllocation().orElseThrow().getResource().getId(),
                "Capacity-optimized strategy must select the smallest suitable resource (RES-2)");

        // 2. PriorityFirst selects the first available candidate (auditorium RES-1)
        request.resetToPending();
        AllocationResult priorityResult = priorityStrategy.allocate(request, candidates, List.of());
        assertTrue(priorityResult.isApproved());
        assertEquals("RES-1", priorityResult.getAllocation().orElseThrow().getResource().getId(),
                "Priority-first selects deterministic first candidate (RES-1)");
    }

    @Test
    @DisplayName("Should prove FairnessAware penalizes requesters with heavy prior allocation loads")
    void testFairnessAwareVsPriorityFirst() {
        FairnessAwareAllocationStrategy fairnessStrategy = new FairnessAwareAllocationStrategy();
        PriorityFirstAllocationStrategy priorityStrategy = new PriorityFirstAllocationStrategy();

        Department highDept = new Department("REQ-HIGH", "High Dept", "h@c.edu", PriorityTier.CRITICAL, "H", "Lead");
        StudentClub lowClub = new StudentClub("REQ-LOW", "Low Club", "l@c.edu", PriorityTier.LOW, "L", "Advisor");

        TimeSlot slot = TimeSlot.of(LocalDateTime.of(2026, 11, 12, 10, 0), 120);

        // Suppose High Dept already has 4 prior active allocations during the week
        List<Allocation> existing = new ArrayList<>();
        Classroom dummyRoom = new Classroom("RES-DUMMY", "Dummy", "Old", 50, "D", true, false);
        for (int i = 1; i <= 4; i++) {
            ResourceRequest priorReq = new ResourceRequestBuilder()
                    .id("PRIOR-" + i)
                    .requester(highDept)
                    .resourceType(ResourceType.CLASSROOM)
                    .requestedTimeSlot(TimeSlot.of(LocalDateTime.of(2026, 11, 1 + i, 9, 0), 60))
                    .requiredCapacity(20)
                    .build();
            existing.add(new Allocation("ALC-" + i, dummyRoom, priorReq, priorReq.getRequestedTimeSlot()));
        }

        ResourceRequest reqFromHigh = new ResourceRequestBuilder()
                .id("REQ-FROM-HIGH")
                .requester(highDept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slot)
                .requiredCapacity(30)
                .priority(RequestPriority.NORMAL)
                .build();

        ResourceRequest reqFromLow = new ResourceRequestBuilder()
                .id("REQ-FROM-LOW")
                .requester(lowClub)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slot)
                .requiredCapacity(30)
                .priority(RequestPriority.NORMAL)
                .build();

        List<ResourceRequest> pending = new ArrayList<>(List.of(reqFromHigh, reqFromLow));

        // 1. Under Priority-First, High Dept always comes first
        Comparator<ResourceRequest> priorityComp = priorityStrategy.getRequestComparator(existing);
        pending.sort(priorityComp);
        assertEquals("REQ-FROM-HIGH", pending.get(0).getId(),
                "Priority-First must place the higher priority requester first");

        // 2. Under Fairness-Aware, High Dept's 4 prior allocations penalize its score below the zero-allocation club!
        Comparator<ResourceRequest> fairnessComp = fairnessStrategy.getRequestComparator(existing);
        pending.sort(fairnessComp);
        assertEquals("REQ-FROM-LOW", pending.get(0).getId(),
                "Fairness-Aware must place the unserviced club first due to fairness penalty on High Dept");
    }
}
