package com.campus.simulator.ui.controller;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.observer.DomainEventListener;
import com.campus.simulator.service.ResourceService;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * Controller mediating operations on campus resources.
 */
public class ResourceController implements DomainEventListener {

    private final ResourceService resourceService;
    private Runnable onDataChangedCallback;

    public ResourceController(SimulationContext context) {
        this.resourceService = context.getResourceService();
        context.getEventPublisher().registerListener(this);
    }

    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    public List<Resource> getAllResources() {
        return resourceService.getAllResources();
    }

    public Resource addResource(ResourceType type,
                                String name,
                                String location,
                                int capacity,
                                Map<String, Object> attributes) {
        return resourceService.addResource(type, name, location, capacity, attributes);
    }

    public void markMaintenance(String resourceId, String reason) {
        resourceService.markUnderMaintenance(resourceId, reason);
    }

    public void markAvailable(String resourceId) {
        resourceService.markAvailable(resourceId);
    }

    public void removeResource(String resourceId) {
        resourceService.removeResource(resourceId);
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (onDataChangedCallback != null) {
            SwingUtilities.invokeLater(onDataChangedCallback);
        }
    }
}
