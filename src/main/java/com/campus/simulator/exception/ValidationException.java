package com.campus.simulator.exception;

/**
 * Thrown when domain validation constraints are violated (e.g., negative capacity, blank names).
 */
public class ValidationException extends CampusSimulatorException {

    public ValidationException(String message) {
        super(message);
    }
}
