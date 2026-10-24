package com.campus.simulator.model;

import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.model.schedule.TimeSlot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimeSlot Value Object Tests")
class TimeSlotTest {

    private final LocalDateTime base = LocalDateTime.of(2026, 10, 20, 10, 0);

    @Test
    @DisplayName("Should successfully create valid TimeSlot and calculate duration")
    void testValidTimeSlotCreation() {
        TimeSlot slot = new TimeSlot(base, base.plusHours(2));
        assertNotNull(slot);
        assertEquals(base, slot.startTime());
        assertEquals(base.plusHours(2), slot.endTime());
        assertEquals(120, slot.durationMinutes());
    }

    @Test
    @DisplayName("Factory method of(start, duration) should construct correct interval")
    void testFactoryMethod() {
        TimeSlot slot = TimeSlot.of(base, 90);
        assertEquals(base, slot.startTime());
        assertEquals(base.plusMinutes(90), slot.endTime());
        assertEquals(90, slot.durationMinutes());
    }

    @Test
    @DisplayName("Should throw ValidationException when end time is before or equal to start time")
    void testInvalidTimeBounds() {
        assertThrows(ValidationException.class, () -> new TimeSlot(base, base));
        assertThrows(ValidationException.class, () -> new TimeSlot(base, base.minusHours(1)));
        assertThrows(ValidationException.class, () -> TimeSlot.of(base, 0));
        assertThrows(ValidationException.class, () -> TimeSlot.of(base, -30));
    }

    @Test
    @DisplayName("Should detect overlapping intervals correctly")
    void testOverlaps() {
        TimeSlot slot1 = TimeSlot.of(base, 120); // 10:00 - 12:00
        TimeSlot slot2 = TimeSlot.of(base.plusHours(1), 120); // 11:00 - 13:00
        TimeSlot slot3 = TimeSlot.of(base.plusHours(2), 60); // 12:00 - 13:00 (abutting boundary)
        TimeSlot slot4 = TimeSlot.of(base.plusHours(3), 60); // 13:00 - 14:00 (completely disjoint)

        assertTrue(slot1.overlaps(slot2), "Overlapping intervals must return true");
        assertTrue(slot2.overlaps(slot1), "Overlap relation must be symmetric");

        // Half-open interval boundary: [10:00, 12:00) and [12:00, 13:00) do NOT overlap
        assertFalse(slot1.overlaps(slot3), "Abutting half-open intervals must not overlap");
        assertFalse(slot1.overlaps(slot4), "Disjoint intervals must not overlap");
    }

    @Test
    @DisplayName("Should evaluate contains() containment logic properly")
    void testContains() {
        TimeSlot outer = TimeSlot.of(base, 180); // 10:00 - 13:00
        TimeSlot inner = TimeSlot.of(base.plusMinutes(30), 60); // 10:30 - 11:30
        TimeSlot partial = TimeSlot.of(base.plusHours(2), 120); // 12:00 - 14:00

        assertTrue(outer.contains(inner));
        assertFalse(inner.contains(outer));
        assertFalse(outer.contains(partial));
    }
}
