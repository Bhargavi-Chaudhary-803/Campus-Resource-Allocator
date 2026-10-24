package com.campus.simulator.ui.controller;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.observer.DomainEventListener;
import com.campus.simulator.service.AllocationService;
import com.campus.simulator.service.ReportingService;

import javax.swing.*;
import java.util.List;

/**
 * Controller mediating data and actions for the Dashboard screen.
 */
public class DashboardController implements DomainEventListener {

    private final ReportingService reportingService;
    private final AllocationService allocationService;
    private Runnable onDataChangedCallback;

    public DashboardController(SimulationContext context) {
        this.reportingService = context.getReportingService();
        this.allocationService = context.getAllocationService();
        context.getEventPublisher().registerListener(this);
    }

    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    public ReportingService.SimulationReport getDashboardMetrics() {
        return reportingService.generateReport();
    }

    public List<Allocation> getRecentAllocations() {
        return allocationService.getAllAllocations();
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (onDataChangedCallback != null) {
            SwingUtilities.invokeLater(onDataChangedCallback);
        }
    }
}
