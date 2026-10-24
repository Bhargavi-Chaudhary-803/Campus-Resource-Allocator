package com.campus.simulator.repository;

import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.util.ValidationUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory repository implementation for Requester entities.
 */
public class InMemoryRequesterRepository implements RequesterRepository {

    private final ConcurrentMap<String, Requester> storage = new ConcurrentHashMap<>();

    @Override
    public Requester save(Requester requester) {
        ValidationUtils.requireNonNull(requester, "requester");
        storage.put(requester.getId(), requester);
        return requester;
    }

    @Override
    public Optional<Requester> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Requester> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Requester::getId))
                .toList();
    }

    @Override
    public List<Requester> findByType(RequesterType type) {
        if (type == null) {
            return List.of();
        }
        return storage.values().stream()
                .filter(r -> r.getRequesterType() == type)
                .sorted(Comparator.comparing(Requester::getId))
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
