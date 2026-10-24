package com.campus.simulator.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe event dispatcher coordinating publisher-subscriber communication
 * across services and UI views.
 */
public class DomainEventPublisher {

    private static final Logger LOGGER = Logger.getLogger(DomainEventPublisher.class.getName());
    private static final DomainEventPublisher INSTANCE = new DomainEventPublisher();

    private final List<DomainEventListener> listeners = new CopyOnWriteArrayList<>();

    public DomainEventPublisher() {
    }

    public static DomainEventPublisher getInstance() {
        return INSTANCE;
    }

    public void registerListener(DomainEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(DomainEventListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public void publish(DomainEvent event) {
        if (event == null) {
            return;
        }
        for (DomainEventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error dispatching event " + event.getEventName(), ex);
            }
        }
    }

    public void clearListeners() {
        listeners.clear();
    }
}
