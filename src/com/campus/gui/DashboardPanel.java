package com.campus.gui;

import com.campus.model.Allocation;
import com.campus.model.AllocationRequest;
import com.campus.model.RequestStatus;
import com.campus.model.Resource;
import com.campus.service.CampusDatabase;
import com.campus.service.SimulationEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: DASHBOARD PANEL
 * =========================================================================
 * Displays high-level campus simulation metrics (Resource Count, Requesters,
 * Allocations, Waitlisted Requests, Overall Utilization %) and a live table
 * of confirmed allocations.
 */
public class DashboardPanel extends JPanel {

    private final CampusDatabase db;
    private final SimulationEngine engine;

    // Stat card labels
    private JLabel totalResourcesLabel;
    private JLabel totalRequestersLabel;
    private JLabel totalRequestsLabel;
    private JLabel allocatedCountLabel;
    private JLabel waitlistCountLabel;
    private JLabel utilizationLabel;

    private JTable allocationsTable;
    private DefaultTableModel tableModel;

    public DashboardPanel(CampusDatabase db, SimulationEngine engine) {
        this.db = db;
        this.engine = engine;
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.OFF_WHITE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- 1. Top Header & Metric Cards ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 14));
        topPanel.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Campus Resource Overview");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Live real-time status of university rooms, laboratories, equipment, and scheduling queue.");
        subtitle.setFont(UITheme.regular(12.0f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Metric Summary Cards (6-card grid)
        JPanel statsGrid = new JPanel(new GridLayout(1, 6, 12, 0));
        statsGrid.setOpaque(false);

        totalResourcesLabel = new JLabel("0");
        totalRequestersLabel = new JLabel("0");
        totalRequestsLabel = new JLabel("0");
        allocatedCountLabel = new JLabel("0");
        waitlistCountLabel = new JLabel("0");
        utilizationLabel = new JLabel("0%");

        statsGrid.add(createMetricCard("Total Resources", totalResourcesLabel, UITheme.PRIMARY_BLUE));
        statsGrid.add(createMetricCard("Registered Entities", totalRequestersLabel, UITheme.TEXT_PRIMARY));
        statsGrid.add(createMetricCard("Total Requests", totalRequestsLabel, UITheme.TEXT_PRIMARY));
        statsGrid.add(createMetricCard("Allocated", allocatedCountLabel, UITheme.BADGE_GREEN_TEXT));
        statsGrid.add(createMetricCard("Waitlisted / Conflicts", waitlistCountLabel, UITheme.BADGE_AMBER_TEXT));
        statsGrid.add(createMetricCard("Utilization Rate", utilizationLabel, UITheme.PRIMARY_BLUE));

        topPanel.add(statsGrid, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center: Confirmed Allocations Table Card ---
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.createCardBorder());

        // Table Header Bar
        JPanel tableHeaderBar = new JPanel(new BorderLayout());
        tableHeaderBar.setOpaque(false);

        JLabel tableTitle = new JLabel("Active Allocations & Confirmed Bookings");
        tableTitle.setFont(UITheme.bold(14.0f));
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel strategyInfo = new JLabel("Engine Strategy: " + engine.getCurrentStrategy().getName());
        strategyInfo.setFont(UITheme.bold(11.0f));
        strategyInfo.setForeground(UITheme.PRIMARY_BLUE);

        tableHeaderBar.add(tableTitle, BorderLayout.WEST);
        tableHeaderBar.add(strategyInfo, BorderLayout.EAST);
        tableCard.add(tableHeaderBar, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Allocation ID", "Resource Allocated", "Type", "Requester Entity", "Scheduled Slot", "Duration", "Est. Cost ($)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allocationsTable = new JTable(tableModel);
        UITheme.styleTable(allocationsTable);
        allocationsTable.getColumnModel().getColumn(7).setCellRenderer(UITheme.createStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(allocationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.createCardBorder());

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.regular(11.0f));
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(UITheme.bold(22.0f));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Refreshes the metrics and data table from the in-memory database.
     */
    public void refreshData() {
        // [JAVA CONCEPT - STREAMS & LAMBDAS]: Calculating metrics reactively
        int totalRes = db.getResources().size();
        int totalReq = db.getRequesters().size();
        int totalRqsts = db.getRequests().size();

        long allocatedCount = db.getRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.ALLOCATED)
                .count();

        long waitlistedCount = db.getRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.WAITLISTED || r.getStatus() == RequestStatus.REJECTED)
                .count();

        long bookedResources = db.getResources().stream()
                .filter(Resource::isBooked)
                .count();

        double utilPercent = totalRes > 0 ? ((double) bookedResources / totalRes) * 100.0 : 0.0;

        totalResourcesLabel.setText(String.valueOf(totalRes));
        totalRequestersLabel.setText(String.valueOf(totalReq));
        totalRequestsLabel.setText(String.valueOf(totalRqsts));
        allocatedCountLabel.setText(String.valueOf(allocatedCount));
        waitlistCountLabel.setText(String.valueOf(waitlistedCount));
        utilizationLabel.setText(String.format("%.1f%%", utilPercent));

        // Populate Allocations Table
        tableModel.setRowCount(0);
        List<Allocation> list = db.getAllocations();
        for (Allocation a : list) {
            tableModel.addRow(new Object[]{
                    a.getAllocationId(),
                    a.getResource().getName(),
                    a.getResource().getType().getDisplayName(),
                    a.getRequest().getRequester().getName(),
                    a.getTimeSlot().getFormatted(),
                    a.getTimeSlot().getDurationHours() + " hrs",
                    String.format("$%.2f", a.getTotalEstimatedCost()),
                    a.isActive() ? "Allocated" : "Released"
            });
        }
    }
}
