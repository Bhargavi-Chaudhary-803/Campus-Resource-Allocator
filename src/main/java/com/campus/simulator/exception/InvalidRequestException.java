package com.campus.simulator.exception;

/**
 * Thrown when a resource allocation request is invalid or violates domain rules.
 */
public class InvalidRequestException extends ValidationException {

    private final String requestId;

    public InvalidRequestException(String message) {
        super(message);
        this.requestId = null;
    }

    public InvalidRequestException(String requestId, String message) {
        super(String.format("Request [%s] invalid: %s", requestId, message));
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
