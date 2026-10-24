package com.campus.simulator.repository;

import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.util.ValidationUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory repository implementation for ResourceRequest entities.
 */
public class InMemoryResourceRequestRepository implements ResourceRequestRepository {

    private final ConcurrentMap<String, ResourceRequest> storage = new ConcurrentHashMap<>();

    @Override
    public ResourceRequest save(ResourceRequest request) {
        ValidationUtils.requireNonNull(request, "request");
        storage.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<ResourceRequest> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ResourceRequest> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(ResourceRequest::getId))
                .toList();
    }

    @Override
    public List<ResourceRequest> findByStatus(RequestStatus status) {
        if (status == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(req -> req.getStatus() == status)
                .sorted(Comparator.comparing(ResourceRequest::getId))
                .toList();
    }

    @Override
    public List<ResourceRequest> findByRequesterId(String requesterId) {
        if (requesterId == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(req -> req.getRequester().getId().equalsIgnoreCase(requesterId))
                .sorted(Comparator.comparing(ResourceRequest::getId))
                .toList();
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
