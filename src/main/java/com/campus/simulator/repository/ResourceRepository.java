package com.campus.simulator.repository;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceStatus;
import com.campus.simulator.model.resource.ResourceType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface defining persistence abstractions for Resource entities.
 */
public interface ResourceRepository {

    Resource save(Resource resource);

    Optional<Resource> findById(String id);

    List<Resource> findAll();

    List<Resource> findByType(ResourceType type);

    List<Resource> findByStatus(ResourceStatus status);

    boolean deleteById(String id);

    boolean existsById(String id);

    long count();

    void clear();
}
