package com.campus.simulator.model.schedule;

import com.campus.simulator.exception.ValidationException;
import com.campus.simulator.util.DateTimeUtils;
import com.campus.simulator.util.ValidationUtils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Immutable value object representing a scheduled time interval.
 *
 * @param startTime the start date and time (inclusive)
 * @param endTime   the end date and time (exclusive)
 */
public record TimeSlot(LocalDateTime startTime, LocalDateTime endTime) implements Comparable<TimeSlot> {

    public TimeSlot {
        ValidationUtils.requireNonNull(startTime, "startTime");
        ValidationUtils.requireNonNull(endTime, "endTime");
        if (!endTime.isAfter(startTime)) {
            throw new ValidationException(String.format(
                    "End time (%s) must be strictly after start time (%s)",
                    endTime, startTime));
        }
    }

    /**
     * Factory method creating a TimeSlot from a start time and duration in minutes.
     */
    public static TimeSlot of(LocalDateTime startTime, long durationMinutes) {
        ValidationUtils.requireNonNull(startTime, "startTime");
        if (durationMinutes <= 0) {
            throw new ValidationException("Duration minutes must be strictly positive, got: " + durationMinutes);
        }
        return new TimeSlot(startTime, startTime.plusMinutes(durationMinutes));
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     * Two intervals [s1, e1) and [s2, e2) overlap if and only if s1 < e2 and s2 < e1.
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    /**
     * Checks if this time slot completely encloses the given other time slot.
     */
    public boolean contains(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return !other.startTime.isBefore(this.startTime) && !other.endTime.isAfter(this.endTime);
    }

    /**
     * Returns the duration of this time slot in minutes.
     */
    public long durationMinutes() {
        return Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Returns the duration as a java.time.Duration object.
     */
    public Duration toDuration() {
        return Duration.between(startTime, endTime);
    }

    /**
     * Human-readable formatted string: "2026-10-18 09:00 - 11:00 (120 min)"
     */
    public String getFormatted() {
        return String.format("%s - %s (%d min)",
                DateTimeUtils.format(startTime),
                DateTimeUtils.formatTime(endTime),
                durationMinutes());
    }

    @Override
    public int compareTo(TimeSlot o) {
        int cmp = this.startTime.compareTo(o.startTime);
        if (cmp != 0) {
            return cmp;
        }
        return this.endTime.compareTo(o.endTime);
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
