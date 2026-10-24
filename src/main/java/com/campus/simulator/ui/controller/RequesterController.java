package com.campus.simulator.ui.controller;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.observer.DomainEventListener;
import com.campus.simulator.service.RequesterService;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Controller mediating operations on university requesters.
 */
public class RequesterController implements DomainEventListener {

    private final RequesterService requesterService;
    private Runnable onDataChangedCallback;

    public RequesterController(SimulationContext context) {
        this.requesterService = context.getRequesterService();
        context.getEventPublisher().registerListener(this);
    }

    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    public List<Requester> getAllRequesters() {
        return requesterService.getAllRequesters();
    }

    public Requester addRequester(RequesterType type,
                                  String name,
                                  String email,
                                  PriorityTier tier,
                                  Map<String, Object> attributes) {
        return requesterService.addRequester(type, name, email, tier, attributes);
    }

    public void toggleActiveStatus(String requesterId) {
        requesterService.toggleActiveStatus(requesterId);
    }

    public void removeRequester(String requesterId) {
        requesterService.removeRequester(requesterId);
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (onDataChangedCallback != null) {
            SwingUtilities.invokeLater(onDataChangedCallback);
        }
    }
}
