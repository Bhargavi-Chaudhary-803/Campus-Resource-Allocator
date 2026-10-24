package com.campus.simulator.service;

import com.campus.simulator.exception.EntityNotFoundException;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterFactory;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.observer.DomainEventPublisher;
import com.campus.simulator.observer.RequesterAddedEvent;
import com.campus.simulator.repository.RequesterRepository;
import com.campus.simulator.util.IdGenerator;
import com.campus.simulator.util.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Application service managing requester profiles, eligibility, and priority tiers.
 */
public class RequesterService {

    private final RequesterRepository requesterRepository;
    private final DomainEventPublisher eventPublisher;

    public RequesterService(RequesterRepository requesterRepository,
                            DomainEventPublisher eventPublisher) {
        this.requesterRepository = ValidationUtils.requireNonNull(requesterRepository, "requesterRepository");
        this.eventPublisher = ValidationUtils.requireNonNull(eventPublisher, "eventPublisher");
    }

    public Requester addRequester(RequesterType type,
                                 String name,
                                 String email,
                                 PriorityTier tier,
                                 Map<String, Object> attributes) {
        String id = IdGenerator.nextId("REQ");
        Requester requester = RequesterFactory.createRequester(type, id, name, email, tier, attributes);
        return addRequester(requester);
    }

    public Requester addRequester(Requester requester) {
        ValidationUtils.requireNonNull(requester, "requester");
        Requester saved = requesterRepository.save(requester);
        eventPublisher.publish(new RequesterAddedEvent(saved));
        return saved;
    }

    public Optional<Requester> getRequester(String id) {
        return requesterRepository.findById(id);
    }

    public Requester getRequesterOrThrow(String id) {
        return requesterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Requester", id));
    }

    public List<Requester> getAllRequesters() {
        return requesterRepository.findAll();
    }

    public List<Requester> getRequestersByType(RequesterType type) {
        return requesterRepository.findByType(type);
    }

    public void updatePriorityTier(String requesterId, PriorityTier newTier) {
        Requester requester = getRequesterOrThrow(requesterId);
        requester.setPriorityTier(newTier);
        requesterRepository.save(requester);
    }

    public void toggleActiveStatus(String requesterId) {
        Requester requester = getRequesterOrThrow(requesterId);
        if (requester.isActive()) {
            requester.deactivate();
        } else {
            requester.activate();
        }
        requesterRepository.save(requester);
    }

    public boolean removeRequester(String requesterId) {
        ValidationUtils.requireNonBlank(requesterId, "requesterId");
        return requesterRepository.deleteById(requesterId);
    }

    public long getTotalCount() {
        return requesterRepository.count();
    }
}
