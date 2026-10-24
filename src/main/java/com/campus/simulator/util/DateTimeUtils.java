package com.campus.simulator.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Common formatting and parsing utilities for dates and times.
 */
public final class DateTimeUtils {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private DateTimeUtils() {
        // Prevent instantiation
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(TIME_FORMATTER);
    }
}
