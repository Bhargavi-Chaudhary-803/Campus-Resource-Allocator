package com.campus.simulator.ui.controller;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.observer.DomainEventListener;
import com.campus.simulator.service.RequesterService;
import com.campus.simulator.service.ResourceRequestService;
import com.campus.simulator.service.ResourceService;

import javax.swing.*;
import java.util.List;

/**
 * Controller mediating submission, viewing, and cancellation of allocation requests.
 */
public class RequestController implements DomainEventListener {

    private final ResourceRequestService requestService;
    private final RequesterService requesterService;
    private final ResourceService resourceService;
    private Runnable onDataChangedCallback;

    public RequestController(SimulationContext context) {
        this.requestService = context.getRequestService();
        this.requesterService = context.getRequesterService();
        this.resourceService = context.getResourceService();
        context.getEventPublisher().registerListener(this);
    }

    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    public List<ResourceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    public List<Requester> getActiveRequesters() {
        return requesterService.getAllRequesters().stream()
                .filter(Requester::isActive)
                .toList();
    }

    public List<Resource> getAllResources() {
        return resourceService.getAllResources();
    }

    public ResourceRequest submitRequest(String requesterId,
                                         ResourceType resourceType,
                                         String targetResourceId,
                                         TimeSlot timeSlot,
                                         int requiredCapacity,
                                         RequestPriority priority,
                                         String purpose) {
        return requestService.submitRequest(requesterId, resourceType, targetResourceId,
                timeSlot, requiredCapacity, priority, purpose);
    }

    public void cancelRequest(String requestId, String reason) {
        requestService.cancelRequest(requestId, reason);
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (onDataChangedCallback != null) {
            SwingUtilities.invokeLater(onDataChangedCallback);
        }
    }
}
