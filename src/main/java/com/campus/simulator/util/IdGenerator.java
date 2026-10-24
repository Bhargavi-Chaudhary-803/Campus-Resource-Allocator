package com.campus.simulator.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe unique ID generator producing human-readable sequential identifiers.
 * E.g., RES-001, REQ-001, RQST-001, ALC-001.
 */
public final class IdGenerator {

    private static final ConcurrentMap<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();

    private IdGenerator() {
        // Prevent instantiation
    }

    /**
     * Generates a new ID with the given prefix and 3-digit zero padding.
     * E.g. nextId("RES") -> "RES-001"
     */
    public static String nextId(String prefix) {
        AtomicInteger counter = COUNTERS.computeIfAbsent(prefix, p -> new AtomicInteger(0));
        int val = counter.incrementAndGet();
        return String.format("%s-%03d", prefix, val);
    }

    /**
     * Sets or advances the sequence counter for a prefix.
     */
    public static void setCounter(String prefix, int value) {
        COUNTERS.put(prefix, new AtomicInteger(value));
    }

    /**
     * Resets all sequence counters (useful when resetting simulation state).
     */
    public static void reset() {
        COUNTERS.clear();
    }
}
