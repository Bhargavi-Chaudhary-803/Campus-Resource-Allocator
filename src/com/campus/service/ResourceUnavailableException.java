package com.campus.service;

/**
 * =========================================================================
 * JAVA CONCEPT: CUSTOM EXCEPTION HANDLING
 * =========================================================================
 * Thrown when an operation attempts to allocate or manipulate a resource
 * that is currently unavailable (e.g. under maintenance).
 */
public class ResourceUnavailableException extends Exception {

    public ResourceUnavailableException(String message) {
        super(message);
    }
}
