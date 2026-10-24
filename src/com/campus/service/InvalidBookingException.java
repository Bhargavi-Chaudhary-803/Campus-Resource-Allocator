package com.campus.service;

/**
 * =========================================================================
 * JAVA CONCEPT: CUSTOM EXCEPTION HANDLING
 * =========================================================================
 * Thrown when an allocation request violates domain rules or schedule constraints.
 */
public class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}
