package com.campus.simulator.service;

import com.campus.simulator.exception.DuplicateResourceException;
import com.campus.simulator.exception.EntityNotFoundException;
import com.campus.simulator.exception.ResourceUnavailableException;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceFactory;
import com.campus.simulator.model.resource.ResourceStatus;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.observer.DomainEventPublisher;
import com.campus.simulator.observer.ResourceAddedEvent;
import com.campus.simulator.observer.ResourceRemovedEvent;
import com.campus.simulator.observer.ResourceStatusChangedEvent;
import com.campus.simulator.repository.AllocationRepository;
import com.campus.simulator.repository.ResourceRepository;
import com.campus.simulator.util.IdGenerator;
import com.campus.simulator.util.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Application service managing resource inventory, lifecycle state transitions, and validation.
 */
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final AllocationRepository allocationRepository;
    private final DomainEventPublisher eventPublisher;

    public ResourceService(ResourceRepository resourceRepository,
                           AllocationRepository allocationRepository,
                           DomainEventPublisher eventPublisher) {
        this.resourceRepository = ValidationUtils.requireNonNull(resourceRepository, "resourceRepository");
        this.allocationRepository = ValidationUtils.requireNonNull(allocationRepository, "allocationRepository");
        this.eventPublisher = ValidationUtils.requireNonNull(eventPublisher, "eventPublisher");
    }

    public Resource addResource(ResourceType type,
                                String name,
                                String location,
                                int capacity,
                                Map<String, Object> attributes) {
        String id = IdGenerator.nextId("RES");
        Resource resource = ResourceFactory.createResource(type, id, name, location, capacity, attributes);
        return addResource(resource);
    }

    public Resource addResource(Resource resource) {
        ValidationUtils.requireNonNull(resource, "resource");
        if (resourceRepository.existsById(resource.getId())) {
            throw new DuplicateResourceException(resource.getId());
        }
        Resource saved = resourceRepository.save(resource);
        eventPublisher.publish(new ResourceAddedEvent(saved));
        return saved;
    }

    public Optional<Resource> getResource(String id) {
        return resourceRepository.findById(id);
    }

    public Resource getResourceOrThrow(String id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource", id));
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<Resource> getAvailableResources() {
        return resourceRepository.findByStatus(ResourceStatus.AVAILABLE);
    }

    public List<Resource> getResourcesByType(ResourceType type) {
        return resourceRepository.findByType(type);
    }

    public void markUnderMaintenance(String resourceId, String reason) {
        Resource resource = getResourceOrThrow(resourceId);
        ResourceStatus oldStatus = resource.getStatus();
        resource.markUnderMaintenance(reason);
        resourceRepository.save(resource);
        eventPublisher.publish(new ResourceStatusChangedEvent(resource, oldStatus, ResourceStatus.MAINTENANCE));
    }

    public void markAvailable(String resourceId) {
        Resource resource = getResourceOrThrow(resourceId);
        ResourceStatus oldStatus = resource.getStatus();
        resource.markAvailable();
        resourceRepository.save(resource);
        eventPublisher.publish(new ResourceStatusChangedEvent(resource, oldStatus, ResourceStatus.AVAILABLE));
    }

    public void retireResource(String resourceId, String reason) {
        Resource resource = getResourceOrThrow(resourceId);
        ResourceStatus oldStatus = resource.getStatus();
        resource.retire(reason);
        resourceRepository.save(resource);
        eventPublisher.publish(new ResourceStatusChangedEvent(resource, oldStatus, ResourceStatus.RETIRED));
    }

    public boolean removeResource(String resourceId) {
        ValidationUtils.requireNonBlank(resourceId, "resourceId");
        boolean removed = resourceRepository.deleteById(resourceId);
        if (removed) {
            eventPublisher.publish(new ResourceRemovedEvent(resourceId));
        }
        return removed;
    }

    public long getTotalCount() {
        return resourceRepository.count();
    }
}
