package com.campus.simulator.ui.controller;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.observer.DomainEvent;
import com.campus.simulator.observer.DomainEventListener;
import com.campus.simulator.service.ReportingService;

import javax.swing.*;

/**
 * Controller mediating statistical reporting and capacity audits.
 */
public class ReportController implements DomainEventListener {

    private final ReportingService reportingService;
    private Runnable onDataChangedCallback;

    public ReportController(SimulationContext context) {
        this.reportingService = context.getReportingService();
        context.getEventPublisher().registerListener(this);
    }

    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    public ReportingService.SimulationReport generateReport() {
        return reportingService.generateReport();
    }

    @Override
    public void onEvent(DomainEvent event) {
        if (onDataChangedCallback != null) {
            SwingUtilities.invokeLater(onDataChangedCallback);
        }
    }
}
