package com.campus.simulator.service;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationStatus;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceStatus;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.repository.AllocationRepository;
import com.campus.simulator.repository.RequesterRepository;
import com.campus.simulator.repository.ResourceRepository;
import com.campus.simulator.repository.ResourceRequestRepository;
import com.campus.simulator.util.ValidationUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for statistical analysis, utilization metrics, and audit reporting.
 */
public class ReportingService {

    private final ResourceRepository resourceRepository;
    private final RequesterRepository requesterRepository;
    private final ResourceRequestRepository requestRepository;
    private final AllocationRepository allocationRepository;

    public ReportingService(ResourceRepository resourceRepository,
                            RequesterRepository requesterRepository,
                            ResourceRequestRepository requestRepository,
                            AllocationRepository allocationRepository) {
        this.resourceRepository = ValidationUtils.requireNonNull(resourceRepository, "resourceRepository");
        this.requesterRepository = ValidationUtils.requireNonNull(requesterRepository, "requesterRepository");
        this.requestRepository = ValidationUtils.requireNonNull(requestRepository, "requestRepository");
        this.allocationRepository = ValidationUtils.requireNonNull(allocationRepository, "allocationRepository");
    }

    /**
     * DTO containing all calculated metrics for dashboard and reports.
     */
    public record SimulationReport(
            long totalResources,
            long availableResources,
            long maintenanceResources,
            long retiredResources,
            long totalRequesters,
            long totalRequests,
            long approvedRequests,
            long waitlistedRequests,
            long rejectedRequests,
            long cancelledRequests,
            long pendingRequests,
            double approvalRatePercentage,
            double resourceUtilizationPercentage,
            String mostRequestedResourceType,
            Map<RequesterType, Long> requestsByRequesterType,
            Map<ResourceType, Long> requestsByResourceType,
            Map<String, Long> peakDemandSlots
    ) {}

    public SimulationReport generateReport() {
        List<Resource> resources = resourceRepository.findAll();
        List<ResourceRequest> requests = requestRepository.findAll();
        List<Allocation> allocations = allocationRepository.findAll();

        long totalResources = resources.size();
        long availableResources = resources.stream().filter(r -> r.getStatus() == ResourceStatus.AVAILABLE).count();
        long maintenanceResources = resources.stream().filter(r -> r.getStatus() == ResourceStatus.MAINTENANCE).count();
        long retiredResources = resources.stream().filter(r -> r.getStatus() == ResourceStatus.RETIRED).count();

        long totalRequesters = requesterRepository.count();

        long totalRequests = requests.size();
        long approvedRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.COMPLETED).count();
        long waitlistedRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.WAITLISTED).count();
        long rejectedRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.REJECTED).count();
        long cancelledRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.CANCELLED).count();
        long pendingRequests = requests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count();

        double approvalRate = totalRequests > 0 ? (approvedRequests * 100.0 / totalRequests) : 0.0;

        // Utilization: proportion of available resources currently holding an active allocation
        long activeAllocatedResources = allocations.stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE)
                .map(a -> a.getResource().getId())
                .distinct()
                .count();

        double utilizationRate = availableResources > 0
                ? Math.min(100.0, (activeAllocatedResources * 100.0 / availableResources))
                : 0.0;

        // Breakdown by RequesterType
        Map<RequesterType, Long> requestsByRequesterType = new LinkedHashMap<>();
        for (RequesterType type : RequesterType.values()) {
            long count = requests.stream()
                    .filter(r -> r.getRequester().getRequesterType() == type)
                    .count();
            requestsByRequesterType.put(type, count);
        }

        // Breakdown by ResourceType
        Map<ResourceType, Long> requestsByResourceType = new LinkedHashMap<>();
        for (ResourceType type : ResourceType.values()) {
            long count = requests.stream()
                    .filter(r -> r.getResourceType() == type)
                    .count();
            requestsByResourceType.put(type, count);
        }

        String mostRequestedType = requestsByResourceType.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey().getDisplayName() + " (" + e.getValue() + " requests)")
                .orElse("None");

        // Peak demand slots
        Map<String, Long> peakSlots = requests.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRequestedTimeSlot().getFormatted(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return new SimulationReport(
                totalResources,
                availableResources,
                maintenanceResources,
                retiredResources,
                totalRequesters,
                totalRequests,
                approvedRequests,
                waitlistedRequests,
                rejectedRequests,
                cancelledRequests,
                pendingRequests,
                approvalRate,
                utilizationRate,
                mostRequestedType,
                requestsByRequesterType,
                requestsByResourceType,
                peakSlots
        );
    }
}
