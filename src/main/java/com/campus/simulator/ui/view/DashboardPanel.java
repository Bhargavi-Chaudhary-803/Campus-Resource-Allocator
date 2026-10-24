package com.campus.simulator.ui.view;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.service.ReportingService;
import com.campus.simulator.ui.component.CardPanel;
import com.campus.simulator.ui.component.StatusBadgeRenderer;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.DashboardController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dashboard view providing key performance indicators, resource availability,
 * and recent allocations overview.
 */
public class DashboardPanel extends JPanel {

    private final DashboardController controller;

    private JLabel totalResourcesVal;
    private JLabel availableResourcesVal;
    private JLabel totalRequestsVal;
    private JLabel approvedVal;
    private JLabel waitlistedVal;
    private JLabel utilizationVal;

    private DefaultTableModel allocationsTableModel;
    private JTable allocationsTable;

    public DashboardPanel(DashboardController controller) {
        this.controller = controller;
        this.controller.setOnDataChangedCallback(this::refreshData);

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_APP_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initHeader();
        initMetricsCards();
        initRecentAllocationsSection();

        refreshData();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_APP_BG);

        JLabel title = new JLabel("Campus Resource Allocation Dashboard");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Real-time utilization, availability, and pending allocation telemetry");
        subtitle.setFont(UITheme.regular(12.5f));
        subtitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initMetricsCards() {
        JPanel cardsGrid = new JPanel(new GridLayout(1, 6, 12, 12));
        cardsGrid.setBackground(UITheme.COLOR_APP_BG);

        totalResourcesVal = new JLabel("0", SwingConstants.CENTER);
        availableResourcesVal = new JLabel("0", SwingConstants.CENTER);
        totalRequestsVal = new JLabel("0", SwingConstants.CENTER);
        approvedVal = new JLabel("0", SwingConstants.CENTER);
        waitlistedVal = new JLabel("0", SwingConstants.CENTER);
        utilizationVal = new JLabel("0%", SwingConstants.CENTER);

        cardsGrid.add(createMetricCard("Total Resources", totalResourcesVal, UITheme.COLOR_PRIMARY));
        cardsGrid.add(createMetricCard("Available", availableResourcesVal, UITheme.COLOR_SUCCESS_TEXT));
        cardsGrid.add(createMetricCard("Total Requests", totalRequestsVal, UITheme.COLOR_ACCENT_BLUE));
        cardsGrid.add(createMetricCard("Approved", approvedVal, UITheme.COLOR_SUCCESS_TEXT));
        cardsGrid.add(createMetricCard("Waitlisted", waitlistedVal, UITheme.COLOR_WARNING_TEXT));
        cardsGrid.add(createMetricCard("Utilization", utilizationVal, UITheme.COLOR_PRIMARY));

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.COLOR_APP_BG);
        container.add(cardsGrid, BorderLayout.CENTER);
        container.setBorder(new EmptyBorder(12, 0, 16, 0));

        add(container, BorderLayout.BEFORE_FIRST_LINE); // adjusted in composite layout below
    }

    private JPanel createMetricCard(String label, JLabel valueLabel, Color valueColor) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(4, 4));

        JLabel titleLbl = new JLabel(label, SwingConstants.CENTER);
        titleLbl.setFont(UITheme.regular(11.5f));
        titleLbl.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        valueLabel.setFont(UITheme.bold(22.0f));
        valueLabel.setForeground(valueColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void initRecentAllocationsSection() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setBackground(UITheme.COLOR_APP_BG);

        // Re-add metrics grid at the top of center panel
        JPanel metricsContainer = new JPanel(new GridLayout(1, 6, 12, 12));
        metricsContainer.setBackground(UITheme.COLOR_APP_BG);
        metricsContainer.add(createMetricCard("Total Resources", totalResourcesVal, UITheme.COLOR_PRIMARY));
        metricsContainer.add(createMetricCard("Available", availableResourcesVal, UITheme.COLOR_SUCCESS_TEXT));
        metricsContainer.add(createMetricCard("Total Requests", totalRequestsVal, UITheme.COLOR_ACCENT_BLUE));
        metricsContainer.add(createMetricCard("Approved", approvedVal, UITheme.COLOR_SUCCESS_TEXT));
        metricsContainer.add(createMetricCard("Waitlisted", waitlistedVal, UITheme.COLOR_WARNING_TEXT));
        metricsContainer.add(createMetricCard("Utilization", utilizationVal, UITheme.COLOR_PRIMARY));
        centerPanel.add(metricsContainer, BorderLayout.NORTH);

        CardPanel tableCard = new CardPanel("Recent Allocations");
        tableCard.setLayout(new BorderLayout(0, 10));

        String[] columns = {"Allocation ID", "Resource", "Requester", "Time Slot", "Status"};
        allocationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allocationsTable = new JTable(allocationsTableModel);
        UITheme.styleTable(allocationsTable);
        allocationsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(allocationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        scrollPane.getViewport().setBackground(UITheme.COLOR_CARD_BG);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        ReportingService.SimulationReport report = controller.getDashboardMetrics();

        totalResourcesVal.setText(String.valueOf(report.totalResources()));
        availableResourcesVal.setText(String.valueOf(report.availableResources()));
        totalRequestsVal.setText(String.valueOf(report.totalRequests()));
        approvedVal.setText(String.valueOf(report.approvedRequests()));
        waitlistedVal.setText(String.valueOf(report.waitlistedRequests()));
        utilizationVal.setText(String.format("%.1f%%", report.resourceUtilizationPercentage()));

        allocationsTableModel.setRowCount(0);
        List<Allocation> recent = controller.getRecentAllocations();
        for (Allocation a : recent) {
            allocationsTableModel.addRow(new Object[]{
                    a.getId(),
                    a.getResource().getName() + " (" + a.getResource().getResourceType().getDisplayName() + ")",
                    a.getRequest().getRequester().getName(),
                    a.getApprovedTimeSlot().getFormatted(),
                    a.getStatus()
            });
        }
    }
}
