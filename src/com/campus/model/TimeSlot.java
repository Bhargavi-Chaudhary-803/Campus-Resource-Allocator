package com.campus.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * =========================================================================
 * OOP PRINCIPLE: ENCAPSULATION & IMMUTABILITY (Value Object)
 * =========================================================================
 * Represents a scheduled time window on a specific date.
 * Features interval arithmetic to detect scheduling conflicts and overlaps.
 */
public class TimeSlot {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private final LocalDate date;
    private final int startHour; // 0 to 23
    private final int endHour;   // 1 to 24

    public TimeSlot(LocalDate date, int startHour, int endHour) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        if (startHour < 0 || startHour > 23) {
            throw new IllegalArgumentException("Start hour must be between 0 and 23.");
        }
        if (endHour <= startHour || endHour > 24) {
            throw new IllegalArgumentException("End hour must be strictly after start hour (and <= 24).");
        }
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getDurationHours() {
        return endHour - startHour;
    }

    /**
     * Checks if this time slot collides with another time slot on the same date.
     * Intervals [s1, e1) and [s2, e2) overlap if: s1 < e2 && s2 < e1.
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null) return false;
        if (!this.date.equals(other.date)) return false;
        return this.startHour < other.endHour && other.startHour < this.endHour;
    }

    public String getFormatted() {
        return String.format("%s, %02d:00 - %02d:00 (%d hrs)",
                date.format(DATE_FMT), startHour, endHour, getDurationHours());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot timeSlot)) return false;
        return startHour == timeSlot.startHour && endHour == timeSlot.endHour && Objects.equals(date, timeSlot.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, startHour, endHour);
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
