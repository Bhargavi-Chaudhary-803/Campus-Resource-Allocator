package com.campus.simulator.ui.view;

import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.service.ReportingService;
import com.campus.simulator.ui.component.CardPanel;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.ReportController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Screen presenting campus analytical summaries, capacity utilization,
 * requester activity breakdowns, and peak demand schedules.
 */
public class ReportsPanel extends JPanel {

    private final ReportController controller;

    private JLabel utilizationLbl;
    private JLabel approvalRateLbl;
    private JLabel mostRequestedLbl;
    private JLabel totalRequestsLbl;

    private DefaultTableModel requesterTypeTableModel;
    private DefaultTableModel resourceTypeTableModel;
    private DefaultTableModel peakSlotsTableModel;

    public ReportsPanel(ReportController controller) {
        this.controller = controller;
        this.controller.setOnDataChangedCallback(this::refreshData);

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_APP_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initHeader();
        initContent();

        refreshData();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_APP_BG);

        JLabel title = new JLabel("Campus Resource Utilization & Allocation Reports");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Audit metrics, demand distributions, and congestion hotspots across campus facilities");
        subtitle.setFont(UITheme.regular(12.5f));
        subtitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initContent() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.COLOR_APP_BG);

        // Top Summary Cards
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 12, 12));
        metricsGrid.setBackground(UITheme.COLOR_APP_BG);

        utilizationLbl = new JLabel("0%", SwingConstants.CENTER);
        approvalRateLbl = new JLabel("0%", SwingConstants.CENTER);
        mostRequestedLbl = new JLabel("-", SwingConstants.CENTER);
        totalRequestsLbl = new JLabel("0", SwingConstants.CENTER);

        metricsGrid.add(createReportCard("Resource Utilization", utilizationLbl, UITheme.COLOR_PRIMARY));
        metricsGrid.add(createReportCard("Overall Approval Rate", approvalRateLbl, UITheme.COLOR_SUCCESS_TEXT));
        metricsGrid.add(createReportCard("Total Demand Requests", totalRequestsLbl, UITheme.COLOR_ACCENT_BLUE));
        metricsGrid.add(createReportCard("Highest In-Demand Asset", mostRequestedLbl, UITheme.COLOR_PRIMARY));

        centerPanel.add(metricsGrid, BorderLayout.NORTH);

        // Tables Grid: Requester Types, Resource Types, Peak Demand Slots
        JPanel tablesGrid = new JPanel(new GridLayout(1, 3, 14, 14));
        tablesGrid.setBackground(UITheme.COLOR_APP_BG);

        // 1. Demand by Requester Type
        CardPanel requesterCard = new CardPanel("Requests by Requester Type");
        String[] reqCols = {"Requester Classification", "Total Requests", "Share %"};
        requesterTypeTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable reqTable = new JTable(requesterTypeTableModel);
        UITheme.styleTable(reqTable);
        JScrollPane reqScroll = new JScrollPane(reqTable);
        reqScroll.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        requesterCard.add(reqScroll, BorderLayout.CENTER);

        // 2. Demand by Resource Type
        CardPanel resourceCard = new CardPanel("Demand by Resource Category");
        String[] resCols = {"Resource Category", "Requests", "Demand Index"};
        resourceTypeTableModel = new DefaultTableModel(resCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable resTable = new JTable(resourceTypeTableModel);
        UITheme.styleTable(resTable);
        JScrollPane resScroll = new JScrollPane(resTable);
        resScroll.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        resourceCard.add(resScroll, BorderLayout.CENTER);

        // 3. Peak Congestion Time Slots
        CardPanel slotsCard = new CardPanel("Peak Demand Time Slots");
        String[] slotCols = {"Time Interval", "Concurrent Requests"};
        peakSlotsTableModel = new DefaultTableModel(slotCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable slotsTable = new JTable(peakSlotsTableModel);
        UITheme.styleTable(slotsTable);
        JScrollPane slotsScroll = new JScrollPane(slotsTable);
        slotsScroll.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        slotsCard.add(slotsScroll, BorderLayout.CENTER);

        tablesGrid.add(requesterCard);
        tablesGrid.add(resourceCard);
        tablesGrid.add(slotsCard);

        centerPanel.add(tablesGrid, BorderLayout.CENTER);

        // Refresh action bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(UITheme.COLOR_APP_BG);
        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Analytics");
        refreshBtn.addActionListener(e -> refreshData());
        bottomBar.add(refreshBtn);
        centerPanel.add(bottomBar, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createReportCard(String title, JLabel valueLabel, Color color) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(4, 4));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(UITheme.regular(11.5f));
        titleLbl.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        valueLabel.setFont(UITheme.bold(17.0f));
        valueLabel.setForeground(color);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        ReportingService.SimulationReport report = controller.generateReport();

        utilizationLbl.setText(String.format("%.1f%%", report.resourceUtilizationPercentage()));
        approvalRateLbl.setText(String.format("%.1f%%", report.approvalRatePercentage()));
        totalRequestsLbl.setText(String.valueOf(report.totalRequests()));
        mostRequestedLbl.setText(report.mostRequestedResourceType());

        // Requester type breakdown
        requesterTypeTableModel.setRowCount(0);
        long totalReq = Math.max(1, report.totalRequests());
        for (Map.Entry<RequesterType, Long> entry : report.requestsByRequesterType().entrySet()) {
            double share = (entry.getValue() * 100.0) / totalReq;
            requesterTypeTableModel.addRow(new Object[]{
                    entry.getKey().getDisplayName(),
                    entry.getValue(),
                    String.format("%.1f%%", share)
            });
        }

        // Resource type breakdown
        resourceTypeTableModel.setRowCount(0);
        for (Map.Entry<ResourceType, Long> entry : report.requestsByResourceType().entrySet()) {
            resourceTypeTableModel.addRow(new Object[]{
                    entry.getKey().getDisplayName(),
                    entry.getValue(),
                    entry.getValue() > 0 ? "High" : "Low"
            });
        }

        // Peak slots
        peakSlotsTableModel.setRowCount(0);
        for (Map.Entry<String, Long> entry : report.peakDemandSlots().entrySet()) {
            peakSlotsTableModel.addRow(new Object[]{
                    entry.getKey(),
                    entry.getValue() + " requests"
            });
        }
    }
}
