package com.campus.simulator.repository;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationStatus;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.util.ValidationUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory repository implementation for Allocation entities.
 */
public class InMemoryAllocationRepository implements AllocationRepository {

    private final ConcurrentMap<String, Allocation> storage = new ConcurrentHashMap<>();

    @Override
    public Allocation save(Allocation allocation) {
        ValidationUtils.requireNonNull(allocation, "allocation");
        storage.put(allocation.getId(), allocation);
        return allocation;
    }

    @Override
    public Optional<Allocation> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Allocation> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Allocation::getId))
                .toList();
    }

    @Override
    public List<Allocation> findByStatus(AllocationStatus status) {
        if (status == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(a -> a.getStatus() == status)
                .sorted(Comparator.comparing(Allocation::getId))
                .toList();
    }

    @Override
    public List<Allocation> findByResourceId(String resourceId) {
        if (resourceId == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(a -> a.getResource().getId().equalsIgnoreCase(resourceId))
                .sorted(Comparator.comparing(Allocation::getId))
                .toList();
    }

    @Override
    public List<Allocation> findByRequesterId(String requesterId) {
        if (requesterId == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(a -> a.getRequest().getRequester().getId().equalsIgnoreCase(requesterId))
                .sorted(Comparator.comparing(Allocation::getId))
                .toList();
    }

    @Override
    public List<Allocation> findActiveOverlapping(String resourceId, TimeSlot slot) {
        if (resourceId == null || slot == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(a -> a.getResource().getId().equalsIgnoreCase(resourceId))
                .filter(a -> a.isActiveDuring(slot))
                .sorted(Comparator.comparing(Allocation::getId))
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
