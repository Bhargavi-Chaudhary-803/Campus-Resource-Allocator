package com.campus.simulator.model;

import com.campus.simulator.exception.InvalidRequestException;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.allocation.ResourceRequestBuilder;
import com.campus.simulator.model.requester.Department;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceRequest Domain Entity & State Machine Tests")
class ResourceRequestTest {

    private Department activeDepartment;
    private Department inactiveDepartment;
    private TimeSlot sampleSlot;

    @BeforeEach
    void setUp() {
        activeDepartment = new Department("REQ-1", "Math Dept", "math@campus.edu",
                PriorityTier.HIGH, "MATH", "Dr. Gauss");
        inactiveDepartment = new Department("REQ-2", "History Dept", "history@campus.edu",
                PriorityTier.LOW, "HIST", "Dr. Herodotus");
        inactiveDepartment.deactivate();

        sampleSlot = TimeSlot.of(LocalDateTime.of(2026, 11, 1, 9, 0), 120);
    }

    @Test
    @DisplayName("Should create valid ResourceRequest and calculate effective priority")
    void testRequestCreation() {
        ResourceRequest request = new ResourceRequestBuilder()
                .id("RQST-100")
                .requester(activeDepartment)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(sampleSlot)
                .requiredCapacity(40)
                .priority(RequestPriority.URGENT)
                .purpose("Midterm Exam")
                .build();

        assertEquals(RequestStatus.PENDING, request.getStatus());
        assertEquals("Midterm Exam", request.getPurpose());
        assertTrue(request.calculateEffectivePriority() > activeDepartment.getAllocationPriority());
    }

    @Test
    @DisplayName("Should reject request creation for deactivated requesters")
    void testInactiveRequesterRejection() {
        assertThrows(InvalidRequestException.class, () ->
                new ResourceRequestBuilder()
                        .id("RQST-101")
                        .requester(inactiveDepartment)
                        .resourceType(ResourceType.CLASSROOM)
                        .requestedTimeSlot(sampleSlot)
                        .requiredCapacity(20)
                        .build()
        );
    }

    @Test
    @DisplayName("Should enforce controlled state transitions through domain methods")
    void testStateTransitions() {
        ResourceRequest request = new ResourceRequestBuilder()
                .id("RQST-102")
                .requester(activeDepartment)
                .resourceType(ResourceType.CLASSROOM)
                .requestedTimeSlot(sampleSlot)
                .requiredCapacity(30)
                .build();

        assertEquals(RequestStatus.PENDING, request.getStatus());

        // Waitlist
        request.waitlist("No room available");
        assertEquals(RequestStatus.WAITLISTED, request.getStatus());
        assertEquals("No room available", request.getStatusReason());

        // Approve
        request.approve();
        assertEquals(RequestStatus.APPROVED, request.getStatus());

        // Complete
        request.complete();
        assertEquals(RequestStatus.COMPLETED, request.getStatus());

        // Terminal state guards
        assertThrows(InvalidRequestException.class, () -> request.cancel("Too late"));
        assertThrows(InvalidRequestException.class, request::approve);
        assertThrows(InvalidRequestException.class, () -> request.waitlist("Room full"));
    }
}
