package com.campus.service;

import com.campus.model.Allocation;
import com.campus.model.AllocationRequest;
import com.campus.model.RequestStatus;
import com.campus.model.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * =========================================================================
 * JAVA CONCEPT: FILE INPUT / OUTPUT (I/O) & REPORT GENERATION
 * =========================================================================
 * Demonstrates standard Java file handling using FileWriter and PrintWriter.
 * Exports formatted simulation reports to disk for audit and academic records.
 */
public class FileReportService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    /**
     * Exports an audit summary report to the specified file path.
     * @param targetFile destination file.
     * @param strategyName name of the allocation strategy used.
     * @throws IOException if disk write fails.
     */
    public static void exportReportToFile(File targetFile, String strategyName) throws IOException {
        CampusDatabase db = CampusDatabase.getInstance();

        List<Resource> resources = db.getResources();
        List<AllocationRequest> requests = db.getRequests();
        List<Allocation> allocations = db.getAllocations();

        long allocatedCount = requests.stream().filter(r -> r.getStatus() == RequestStatus.ALLOCATED).count();
        long waitlistedCount = requests.stream().filter(r -> r.getStatus() == RequestStatus.WAITLISTED).count();
        long rejectedCount = requests.stream().filter(r -> r.getStatus() == RequestStatus.REJECTED).count();

        double totalCost = allocations.stream().mapToDouble(Allocation::getTotalEstimatedCost).sum();
        double utilizationRate = resources.isEmpty() ? 0.0 : ((double) allocations.size() / resources.size()) * 100.0;

        // [JAVA CONCEPT - TRY-WITH-RESOURCES]: Automatically closes the file stream
        try (PrintWriter writer = new PrintWriter(new FileWriter(targetFile))) {
            writer.println("================================================================================");
            writer.println("                  CAMPUS RESOURCE ALLOCATION SIMULATOR REPORT                   ");
            writer.println("================================================================================");
            writer.println("Generated At:     " + LocalDateTime.now().format(TIME_FMT));
            writer.println("Applied Strategy: " + strategyName);
            writer.println("--------------------------------------------------------------------------------");
            writer.println("SUMMARY STATISTICS:");
            writer.println("  * Total Resources in Inventory: " + resources.size());
            writer.println("  * Total Requesters Registered:  " + db.getRequesters().size());
            writer.println("  * Total Requests Submitted:     " + requests.size());
            writer.println("  * Requests Successfully Booked: " + allocatedCount);
            writer.println("  * Requests Waitlisted:          " + waitlistedCount);
            writer.println("  * Requests Rejected (Policy):   " + rejectedCount);
            writer.printf("  * Resource Utilization Index:   %.1f%%\n", utilizationRate);
            writer.printf("  * Total Estimated Energy Cost:  $%.2f\n", totalCost);
            writer.println("--------------------------------------------------------------------------------");
            writer.println();

            writer.println("APPROVED ALLOCATIONS & RESERVATIONS:");
            writer.printf("%-10s | %-28s | %-26s | %-10s\n", "Alloc ID", "Resource Allocated", "Requester", "Est. Cost");
            writer.println("--------------------------------------------------------------------------------");
            for (Allocation a : allocations) {
                writer.printf("%-10s | %-28s | %-26s | $%.2f\n",
                        a.getAllocationId(),
                        a.getResource().getName(),
                        a.getRequest().getRequester().getName(),
                        a.getTotalEstimatedCost());
                writer.printf("   Time: %s\n", a.getTimeSlot().getFormatted());
            }
            writer.println();

            writer.println("WAITLISTED & CONFLICT DETAILS:");
            writer.println("--------------------------------------------------------------------------------");
            for (AllocationRequest r : requests) {
                if (r.getStatus() == RequestStatus.WAITLISTED || r.getStatus() == RequestStatus.REJECTED) {
                    writer.printf("[%s] %s by %s: %s\n",
                            r.getRequestId(),
                            r.getRequestedType().getDisplayName(),
                            r.getRequester().getName(),
                            r.getStatus());
                    writer.printf("   Reason: %s\n", r.getDecisionExplanation());
                    writer.println();
                }
            }
            writer.println("================================================================================");
            writer.println("                          END OF SIMULATION REPORT                              ");
            writer.println("================================================================================");
        }
    }
}
