package com.campus.simulator.repository;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationStatus;
import com.campus.simulator.model.schedule.TimeSlot;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface defining persistence abstractions for Allocation entities.
 */
public interface AllocationRepository {

    Allocation save(Allocation allocation);

    Optional<Allocation> findById(String id);

    List<Allocation> findAll();

    List<Allocation> findByStatus(AllocationStatus status);

    List<Allocation> findByResourceId(String resourceId);

    List<Allocation> findByRequesterId(String requesterId);

    List<Allocation> findActiveOverlapping(String resourceId, TimeSlot slot);

    boolean deleteById(String id);

    boolean existsById(String id);

    long count();

    void clear();
}
