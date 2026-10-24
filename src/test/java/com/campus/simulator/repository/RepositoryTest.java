package com.campus.simulator.repository;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationStatus;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.allocation.ResourceRequestBuilder;
import com.campus.simulator.model.requester.Department;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.resource.Classroom;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceStatus;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("In-Memory Repository Persistence Tests")
class RepositoryTest {

    private InMemoryResourceRepository resourceRepo;
    private InMemoryRequesterRepository requesterRepo;
    private InMemoryResourceRequestRepository requestRepo;
    private InMemoryAllocationRepository allocationRepo;

    @BeforeEach
    void setUp() {
        resourceRepo = new InMemoryResourceRepository();
        requesterRepo = new InMemoryRequesterRepository();
        requestRepo = new InMemoryResourceRequestRepository();
        allocationRepo = new InMemoryAllocationRepository();
    }

    @Test
    @DisplayName("Should store and query resources by type and operational status")
    void testResourceRepository() {
        Resource r1 = new Classroom("RES-1", "Newton 1", "Quad", 50, "1", true, true);
        Resource r2 = new Classroom("RES-2", "Newton 2", "Quad", 60, "2", true, true);
        r2.markUnderMaintenance("Painting");

        resourceRepo.save(r1);
        resourceRepo.save(r2);

        assertEquals(2, resourceRepo.count());
        assertTrue(resourceRepo.existsById("RES-1"));
        assertEquals(2, resourceRepo.findByType(ResourceType.CLASSROOM).size());
        assertEquals(1, resourceRepo.findByStatus(ResourceStatus.AVAILABLE).size());
        assertEquals(1, resourceRepo.findByStatus(ResourceStatus.MAINTENANCE).size());

        resourceRepo.deleteById("RES-1");
        assertEquals(1, resourceRepo.count());
    }

    @Test
    @DisplayName("Should find active overlapping allocations on a specific resource")
    void testAllocationRepositoryOverlaps() {
        Resource hall = new Classroom("RES-1", "Hall", "Quad", 100, "1", true, true);
        Department dept = new Department("REQ-1", "Math", "m@c.edu", PriorityTier.HIGH, "M", "Lead");

        LocalDateTime t1 = LocalDateTime.of(2026, 10, 25, 9, 0);
        TimeSlot slot1 = TimeSlot.of(t1, 120); // 09:00 - 11:00
        TimeSlot slot2 = TimeSlot.of(t1.plusHours(1), 120); // 10:00 - 12:00 (overlaps)
        TimeSlot slot3 = TimeSlot.of(t1.plusHours(3), 60); // 12:00 - 13:00 (disjoint)

        ResourceRequest req1 = new ResourceRequestBuilder()
                .id("RQST-1")
                .requester(dept)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(slot1)
                .requiredCapacity(50)
                .build();

        Allocation allocation = new Allocation("ALC-1", hall, req1, slot1);
        allocationRepo.save(allocation);

        // Overlapping slot lookup
        List<Allocation> overlapping = allocationRepo.findActiveOverlapping("RES-1", slot2);
        assertEquals(1, overlapping.size());
        assertEquals("ALC-1", overlapping.get(0).getId());

        // Disjoint slot lookup
        List<Allocation> disjoint = allocationRepo.findActiveOverlapping("RES-1", slot3);
        assertTrue(disjoint.isEmpty());
    }
}
