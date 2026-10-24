package com.campus.simulator.model;

import com.campus.simulator.exception.ResourceUnavailableException;
import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.model.resource.Classroom;
import com.campus.simulator.model.resource.Laboratory;
import com.campus.simulator.model.resource.LaptopSet;
import com.campus.simulator.model.resource.PortableResource;
import com.campus.simulator.model.resource.Projector;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceFactory;
import com.campus.simulator.model.resource.ResourceStatus;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.resource.SportsFacility;
import com.campus.simulator.model.resource.StudyRoom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Resource Domain Model & Polymorphism Tests")
class ResourceModelTest {

    @Test
    @DisplayName("Should create concrete Resource subclasses and process mixed list polymorphically")
    void testPolymorphicResources() {
        Resource classroom = new Classroom("RES-001", "Hall A", "Building 1", 100, "101", true, true);
        Resource lab = new Laboratory("RES-002", "CS Lab", "Building 2", 40, "Computer Science", "General");
        Resource projector = new Projector("RES-003", "4K Unit", "Depot", 4000, "4K UHD", true, "Depot Bay");
        Resource laptops = new LaptopSet("RES-004", "Exam Cart", "IT Office", 25, "Ubuntu Linux", "IT Locker");
        Resource gym = new SportsFacility("RES-005", "Arena", "Sports Center", 200, "Basketball", false);
        Resource study = new StudyRoom("RES-006", "Quiet Pod", "Library", 6, true, true);

        List<Resource> inventory = List.of(classroom, lab, projector, laptops, gym, study);
        assertEquals(6, inventory.size());

        for (Resource r : inventory) {
            assertTrue(r.isBookable());
            assertTrue(r.getCapacity() > 0);
            assertNotNull(r.getSpecificDetailsSummary());
            assertFalse(r.getSpecificDetailsSummary().isBlank());
        }

        // Verify PortableResource interface polymorphism
        assertTrue(projector instanceof PortableResource);
        assertTrue(laptops instanceof PortableResource);
        assertFalse(classroom instanceof PortableResource);
    }

    @Test
    @DisplayName("Should test Maintainable capability state transitions")
    void testMaintainableTransitions() {
        Resource room = new Classroom("RES-010", "Seminar Room", "Old Main", 30, "OM-1", true, false);
        assertEquals(ResourceStatus.AVAILABLE, room.getStatus());
        assertTrue(room.isBookable());

        // Put in maintenance
        room.markUnderMaintenance("Repainting walls");
        assertEquals(ResourceStatus.MAINTENANCE, room.getStatus());
        assertEquals("Repainting walls", room.getStatusReason());
        assertFalse(room.isBookable());

        // Restore to available
        room.markAvailable();
        assertEquals(ResourceStatus.AVAILABLE, room.getStatus());
        assertNull(room.getStatusReason());
        assertTrue(room.isBookable());

        // Retire permanently
        room.retire("Building decommissioned");
        assertEquals(ResourceStatus.RETIRED, room.getStatus());
        assertFalse(room.isBookable());

        // Cannot restore or put retired resource into maintenance
        assertThrows(ResourceUnavailableException.class, room::markAvailable);
        assertThrows(ResourceUnavailableException.class, () -> room.markUnderMaintenance("Test"));
    }

    @Test
    @DisplayName("Should enforce capacity accommodation checks correctly")
    void testCapacityProvider() {
        Resource hall = new Classroom("RES-020", "Lecture Hall", "West Quad", 120, "W-1", true, true);
        assertTrue(hall.canAccommodate(50));
        assertTrue(hall.canAccommodate(120));
        assertFalse(hall.canAccommodate(121));
    }

    @Test
    @DisplayName("Should throw ValidationException on invalid resource parameters")
    void testValidationConstraints() {
        assertThrows(ValidationException.class, () ->
                new Classroom(null, "Name", "Loc", 50, "1", true, true));
        assertThrows(ValidationException.class, () ->
                new Classroom("ID", "", "Loc", 50, "1", true, true));
        assertThrows(ValidationException.class, () ->
                new Classroom("ID", "Name", "Loc", 0, "1", true, true)); // 0 capacity
        assertThrows(ValidationException.class, () ->
                new Classroom("ID", "Name", "Loc", -10, "1", true, true)); // negative capacity
    }

    @Test
    @DisplayName("Should create resources accurately using ResourceFactory")
    void testResourceFactory() {
        Resource r = ResourceFactory.createResource(
                ResourceType.LABORATORY,
                "RES-099",
                "Robotics Lab",
                "Tech Center",
                30,
                Map.of("labType", "Robotics", "safetyLevel", "Level-2")
        );
        assertInstanceOf(Laboratory.class, r);
        assertEquals("Robotics Lab", r.getName());
        assertEquals(30, r.getCapacity());
        assertTrue(((Laboratory) r).requiresSafetyClearance());
    }
}
