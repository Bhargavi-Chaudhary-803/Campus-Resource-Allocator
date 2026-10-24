package com.campus.simulator.repository;

import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface defining persistence abstractions for Requester entities.
 */
public interface RequesterRepository {

    Requester save(Requester requester);

    Optional<Requester> findById(String id);

    List<Requester> findAll();

    List<Requester> findByType(RequesterType type);

    boolean deleteById(String id);

    boolean existsById(String id);

    long count();

    void clear();
}
