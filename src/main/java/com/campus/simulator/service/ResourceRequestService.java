package com.campus.simulator.service;

import com.campus.simulator.exception.EntityNotFoundException;
import com.campus.simulator.exception.InvalidRequestException;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.allocation.ResourceRequestBuilder;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.observer.DomainEventPublisher;
import com.campus.simulator.observer.RequestStatusChangedEvent;
import com.campus.simulator.observer.RequestSubmittedEvent;
import com.campus.simulator.repository.RequesterRepository;
import com.campus.simulator.repository.ResourceRequestRepository;
import com.campus.simulator.util.IdGenerator;
import com.campus.simulator.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Application service coordinating the submission, validation, and cancellation of ResourceRequests.
 */
public class ResourceRequestService {

    private final ResourceRequestRepository requestRepository;
    private final RequesterRepository requesterRepository;
    private final DomainEventPublisher eventPublisher;

    public ResourceRequestService(ResourceRequestRepository requestRepository,
                                  RequesterRepository requesterRepository,
                                  DomainEventPublisher eventPublisher) {
        this.requestRepository = ValidationUtils.requireNonNull(requestRepository, "requestRepository");
        this.requesterRepository = ValidationUtils.requireNonNull(requesterRepository, "requesterRepository");
        this.eventPublisher = ValidationUtils.requireNonNull(eventPublisher, "eventPublisher");
    }

    public ResourceRequest submitRequest(String requesterId,
                                         ResourceType resourceType,
                                         String targetResourceId,
                                         TimeSlot timeSlot,
                                         int requiredCapacity,
                                         RequestPriority priority,
                                         String purpose) {
        Requester requester = requesterRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Requester", requesterId));

        if (!requester.isActive()) {
            throw new InvalidRequestException("Cannot submit request for inactive requester: " + requester.getName());
        }

        String id = IdGenerator.nextId("RQST");
        ResourceRequest request = new ResourceRequestBuilder()
                .id(id)
                .requester(requester)
                .resourceType(resourceType)
                .targetResourceId(targetResourceId)
                .requestedTimeSlot(timeSlot)
                .requiredCapacity(requiredCapacity)
                .priority(priority)
                .purpose(purpose)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return submitRequest(request);
    }

    public ResourceRequest submitRequest(ResourceRequest request) {
        ValidationUtils.requireNonNull(request, "request");
        ResourceRequest saved = requestRepository.save(request);
        eventPublisher.publish(new RequestSubmittedEvent(saved));
        return saved;
    }

    public Optional<ResourceRequest> getRequest(String id) {
        return requestRepository.findById(id);
    }

    public ResourceRequest getRequestOrThrow(String id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ResourceRequest", id));
    }

    public List<ResourceRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<ResourceRequest> getPendingRequests() {
        return requestRepository.findByStatus(RequestStatus.PENDING);
    }

    public List<ResourceRequest> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    public void cancelRequest(String requestId, String reason) {
        ResourceRequest request = getRequestOrThrow(requestId);
        RequestStatus oldStatus = request.getStatus();
        request.cancel(reason);
        requestRepository.save(request);
        eventPublisher.publish(new RequestStatusChangedEvent(request, oldStatus, RequestStatus.CANCELLED, reason));
    }

    public long getTotalCount() {
        return requestRepository.count();
    }
}
