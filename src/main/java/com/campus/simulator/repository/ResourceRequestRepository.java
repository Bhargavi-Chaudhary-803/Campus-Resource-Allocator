package com.campus.simulator.repository;

import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface defining persistence abstractions for ResourceRequest entities.
 */
public interface ResourceRequestRepository {

    ResourceRequest save(ResourceRequest request);

    Optional<ResourceRequest> findById(String id);

    List<ResourceRequest> findAll();

    List<ResourceRequest> findByStatus(RequestStatus status);

    List<ResourceRequest> findByRequesterId(String requesterId);

    boolean deleteById(String id);

    boolean existsById(String id);

    long count();

    void clear();
}
