package com.campus.simulator.ui.controller;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.observer.DomainEventListener;
import com.campus.simulator.service.AllocationService;
import com.campus.simulator.service.ResourceRequestService;
import com.campus.simulator.service.SimulationService;
import com.campus.simulator.service.allocation.AllocationStrategyType;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller mediating batch allocation simulation runs asynchronously via SwingWorker,
 * ensuring non-blocking Event Dispatch Thread behavior.
 */
public class SimulationController implements DomainEventListener {

    private final SimulationService simulationService;
    private final AllocationService allocationService;
    private final ResourceRequestService requestService;
    private Runnable onDataChangedCallback;

    public SimulationController(SimulationContext context) {
        this.simulationService = context.getSimulationService();
        this.allocationService = context.getAllocationService();
        this.requestService = context.getRequestService();
        context.getEventPublisher().registerListener(this);
    }

    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    public List<Allocation> getAllAllocations() {
        return allocationService.getAllAllocations();
    }

    public List<ResourceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    /**
     * Executes the allocation simulation asynchronously off the EDT via SwingWorker.
     */
    public void runSimulationAsync(AllocationStrategyType strategyType,
                                   Runnable onStart,
                                   Consumer<List<AllocationResult>> onDone,
                                   Consumer<Exception> onError) {
        if (onStart != null) {
            onStart.run();
        }

        SwingWorker<List<AllocationResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AllocationResult> doInBackground() {
                return simulationService.runSimulationWith(strategyType);
            }

            @Override
            protected void done() {
                try {
                    List<AllocationResult> results = get();
                    if (onDone != null) {
                        onDone.accept(results);
                    }
                } catch (Exception ex) {
                    if (onError != null) {
                        onError.accept(ex);
                    }
                }
            }
        };

        worker.execute();
    }

    public void resetSimulation() {
        simulationService.resetSimulationWithSeedData();
    }

    public void releaseAllocation(String allocationId, String reason) {
        allocationService.releaseAllocation(allocationId, reason);
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (onDataChangedCallback != null) {
            SwingUtilities.invokeLater(onDataChangedCallback);
        }
    }
}
