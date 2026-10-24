package com.campus.simulator.exception;

/**
 * Base unchecked exception for all domain and operational failures in the simulator.
 */
public class CampusSimulatorException extends RuntimeException {

    public CampusSimulatorException(String message) {
        super(message);
    }

    public CampusSimulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
