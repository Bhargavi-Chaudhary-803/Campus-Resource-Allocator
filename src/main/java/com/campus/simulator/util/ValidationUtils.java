package com.campus.simulator.util;

import com.campus.simulator.exception.ValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility methods for domain validation and defensive programming.
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private ValidationUtils() {
        // Prevent instantiation
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be null or blank");
        }
        return value.trim();
    }

    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        return value;
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be strictly positive (> 0), got: " + value);
        }
        return value;
    }

    public static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName + " cannot be negative, got: " + value);
        }
        return value;
    }

    public static String requireValidEmail(String email, String fieldName) {
        String trimmed = requireNonBlank(email, fieldName);
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(fieldName + " has invalid email format: " + trimmed);
        }
        return trimmed;
    }
}
