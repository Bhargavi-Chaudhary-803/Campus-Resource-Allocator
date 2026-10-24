package com.campus.simulator.service;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.allocation.ResourceRequestBuilder;
import com.campus.simulator.model.requester.Department;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.StudentClub;
import com.campus.simulator.model.resource.Classroom;
import com.campus.simulator.model.resource.Laboratory;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.service.allocation.PriorityFirstAllocationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Allocation Engine Conflict Detection & Policy Enforcement Tests")
class AllocationConflictDetectionTest {

    private PriorityFirstAllocationStrategy strategy;
    private Classroom smallRoom;
    private Laboratory bsl2Lab;
    private Department csDept;
    private StudentClub gamingClub;
    private TimeSlot slotMorning;

    @BeforeEach
    void setUp() {
        strategy = new PriorityFirstAllocationStrategy();
        smallRoom = new Classroom("RES-1", "Room 101", "West Hall", 30, "101", true, false);
        bsl2Lab = new Laboratory("RES-2", "Virology Lab", "Science Quad", 20, "Biochemistry", "BSL-2");

        csDept = new Department("REQ-1", "Computer Science", "cs@campus.edu",
                PriorityTier.HIGH, "CS", "Dr. Vance");
        gamingClub = new StudentClub("REQ-2", "Esports Club", "esports@campus.edu",
                PriorityTier.LOW, "Gaming", "Prof. Miller");

        slotMorning = TimeSlot.of(LocalDateTime.of(2026, 11, 5, 10, 0), 120);
    }

    @Test
    @DisplayName("Should detect physical resource schedule collision and waitlist the conflicting request")
    void testResourceScheduleCollision() {
        ResourceRequest req1 = new ResourceRequestBuilder()
                .id("RQST-1")
                .requester(csDept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(25)
                .build();

        List<Resource> candidates = List.of(smallRoom);
        List<Allocation> existing = new ArrayList<>();

        // First request allocates successfully
        AllocationResult result1 = strategy.allocate(req1, candidates, existing);
        assertTrue(result1.isApproved());
        existing.add(result1.getAllocation().orElseThrow());

        // Second overlapping request targeting the same classroom
        ResourceRequest req2 = new ResourceRequestBuilder()
                .id("RQST-2")
                .requester(gamingClub)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(20)
                .build();

        AllocationResult result2 = strategy.allocate(req2, candidates, existing);
        assertTrue(result2.isWaitlisted());
        assertFalse(result2.getConflictDetails().isEmpty());
        assertTrue(result2.getConflictDetails().get(0).contains("already reserved"));
    }

    @Test
    @DisplayName("Should prevent concurrent double-booking by the same requester across different resources")
    void testRequesterDoubleBookingPrevention() {
        Classroom roomA = new Classroom("RES-1", "Room A", "North Hall", 30, "A", true, true);
        Classroom roomB = new Classroom("RES-2", "Room B", "North Hall", 30, "B", true, true);

        ResourceRequest req1 = new ResourceRequestBuilder()
                .id("RQST-1")
                .requester(csDept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(20)
                .build();

        List<Allocation> existing = new ArrayList<>();
        AllocationResult res1 = strategy.allocate(req1, List.of(roomA, roomB), existing);
        assertTrue(res1.isApproved());
        existing.add(res1.getAllocation().orElseThrow());

        // csDept tries to submit a second request for the exact same time slot
        ResourceRequest req2 = new ResourceRequestBuilder()
                .id("RQST-2")
                .requester(csDept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(20)
                .build();

        AllocationResult res2 = strategy.allocate(req2, List.of(roomA, roomB), existing);
        assertTrue(res2.isWaitlisted());
        assertTrue(res2.getExplanation().contains("Requester already holds active allocation"));
    }

    @Test
    @DisplayName("Should detect capacity insufficiency when request exceeds resource capacity")
    void testCapacityDeficiencyConflict() {
        ResourceRequest largeReq = new ResourceRequestBuilder()
                .id("RQST-3")
                .requester(csDept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(100) // smallRoom only has 30 seats
                .build();

        AllocationResult res = strategy.allocate(largeReq, List.of(smallRoom), List.of());
        assertTrue(res.isWaitlisted());
        assertTrue(res.getConflictDetails().get(0).contains("insufficient for requested"));
    }

    @Test
    @DisplayName("Should reject student clubs requesting unauthorized BSL-2 laboratories")
    void testPolicyRejectionForHazardousLabs() {
        ResourceRequest labReq = new ResourceRequestBuilder()
                .id("RQST-4")
                .requester(gamingClub) // Student Club barred from BSL-2
                .resourceType(ResourceType.LABORATORY)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(15)
                .build();

        AllocationResult res = strategy.allocate(labReq, List.of(bsl2Lab), List.of());
        assertTrue(res.isRejected(), "Candidate barred by safety policy must result in REJECTED");
        assertTrue(res.getConflictDetails().stream().anyMatch(c -> c.contains("lacks authorization/safety clearance")));
    }

    @Test
    @DisplayName("Should block allocation on resources marked under maintenance")
    void testMaintenanceBlock() {
        smallRoom.markUnderMaintenance("HVAC Repair");

        ResourceRequest req = new ResourceRequestBuilder()
                .id("RQST-5")
                .requester(csDept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slotMorning)
                .requiredCapacity(20)
                .build();

        AllocationResult res = strategy.allocate(req, List.of(smallRoom), List.of());
        assertTrue(res.isWaitlisted());
        assertTrue(res.getConflictDetails().stream().anyMatch(c -> c.contains("HVAC Repair")));
    }
}
