package com.campus.gui;

import com.campus.model.Allocation;
import com.campus.model.AllocationRequest;
import com.campus.model.RequestStatus;
import com.campus.model.Resource;
import com.campus.service.CampusDatabase;
import com.campus.service.FileReportService;
import com.campus.service.SimulationEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: AUDIT & REPORT PANEL
 * =========================================================================
 * Demonstrates:
 * - Aggregation and statistical summaries across collections
 * - File I/O report export via FileReportService
 * - Clean monospace formatted report preview
 */
public class ReportPanel extends JPanel {

    private final CampusDatabase db;
    private final SimulationEngine engine;

    private JLabel energyCostLabel;
    private JLabel totalAllocationsLabel;
    private JLabel waitlistCountLabel;
    private JLabel efficiencyLabel;
    private JTextArea reportTextArea;

    public ReportPanel(CampusDatabase db, SimulationEngine engine) {
        this.db = db;
        this.engine = engine;

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.OFF_WHITE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initComponents();
        refreshReport();
    }

    private void initComponents() {
        // --- 1. Top Panel: Header and Export Controls ---
        JPanel topContainer = new JPanel(new BorderLayout(0, 14));
        topContainer.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Audit & Simulation Reporting");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Generate official campus facility utilization summaries, operating costs, and file export reports.");
        subtitle.setFont(UITheme.regular(12.0f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        topContainer.add(titlePanel, BorderLayout.NORTH);

        // Metric summary strip (4 cards)
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        statsGrid.setOpaque(false);

        energyCostLabel = new JLabel("$0.00");
        totalAllocationsLabel = new JLabel("0");
        waitlistCountLabel = new JLabel("0");
        efficiencyLabel = new JLabel("0%");

        statsGrid.add(createMetricCard("Total Operating Cost", energyCostLabel, UITheme.PRIMARY_BLUE));
        statsGrid.add(createMetricCard("Successful Bookings", totalAllocationsLabel, UITheme.BADGE_GREEN_TEXT));
        statsGrid.add(createMetricCard("Conflicts / Waitlisted", waitlistCountLabel, UITheme.BADGE_AMBER_TEXT));
        statsGrid.add(createMetricCard("Allocation Efficiency", efficiencyLabel, UITheme.TEXT_PRIMARY));

        topContainer.add(statsGrid, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // --- 2. Center: Report Preview Card ---
        JPanel reportCard = new JPanel(new BorderLayout(0, 10));
        reportCard.setBackground(UITheme.CARD_BG);
        reportCard.setBorder(UITheme.createCardBorder());

        // Header with export button
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel cardTitle = new JLabel("Simulation Audit Report Preview");
        cardTitle.setFont(UITheme.bold(14.0f));
        cardTitle.setForeground(UITheme.TEXT_PRIMARY);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Report");
        refreshBtn.addActionListener(e -> refreshReport());

        JButton exportBtn = UITheme.createPrimaryButton("💾 Export to File (.txt)");
        exportBtn.addActionListener(e -> handleExportToFile());

        btnPanel.add(refreshBtn);
        btnPanel.add(exportBtn);

        cardHeader.add(cardTitle, BorderLayout.WEST);
        cardHeader.add(btnPanel, BorderLayout.EAST);
        reportCard.add(cardHeader, BorderLayout.NORTH);

        // Text area previewing the formatted report
        reportTextArea = new JTextArea();
        reportTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportTextArea.setEditable(false);
        reportTextArea.setBackground(new Color(248, 250, 252));
        reportTextArea.setBorder(new EmptyBorder(12, 14, 12, 14));

        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        reportCard.add(scrollPane, BorderLayout.CENTER);

        add(reportCard, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.createCardBorder());

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.regular(11.0f));
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(UITheme.bold(20.0f));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void handleExportToFile() {
        try {
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            File targetFile = new File(reportsDir, "simulation_audit_report.txt");
            // [JAVA CONCEPT - FILE I/O]: Exporting formatted audit log to disk
            FileReportService.exportReportToFile(targetFile, engine.getCurrentStrategy().getName());

            JOptionPane.showMessageDialog(this,
                    "Simulation report exported successfully!\nLocation: " + targetFile.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to export report: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshReport() {
        List<Resource> resources = db.getResources();
        List<AllocationRequest> requests = db.getRequests();
        List<Allocation> allocations = db.getAllocations();

        long allocatedCount = requests.stream().filter(r -> r.getStatus() == RequestStatus.ALLOCATED).count();
        long waitlistedCount = requests.stream().filter(r -> r.getStatus() == RequestStatus.WAITLISTED).count();
        long rejectedCount = requests.stream().filter(r -> r.getStatus() == RequestStatus.REJECTED).count();

        double totalCost = allocations.stream().mapToDouble(Allocation::getTotalEstimatedCost).sum();
        double efficiency = requests.isEmpty() ? 0.0 : ((double) allocatedCount / requests.size()) * 100.0;

        energyCostLabel.setText(String.format("$%.2f", totalCost));
        totalAllocationsLabel.setText(String.valueOf(allocatedCount));
        waitlistCountLabel.setText(String.valueOf(waitlistedCount + rejectedCount));
        efficiencyLabel.setText(String.format("%.1f%%", efficiency));

        // Build report text
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(80);
        String subline = "-".repeat(80);

        sb.append(line).append("\n");
        sb.append("                  CAMPUS RESOURCE ALLOCATION SIMULATOR AUDIT                    \n");
        sb.append(line).append("\n");
        sb.append("Generated At:     ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"))).append("\n");
        sb.append("Active Strategy:  ").append(engine.getCurrentStrategy().getName()).append("\n");
        sb.append(subline).append("\n");
        sb.append("CAMPUS INVENTORY & QUEUE METRICS:\n");
        sb.append(String.format("  * Total Resources Managed:     %d facilities/units\n", resources.size()));
        sb.append(String.format("  * Total Requesters Registered: %d departments, faculty & clubs\n", db.getRequesters().size()));
        sb.append(String.format("  * Total Requests Evaluated:    %d submitted\n", requests.size()));
        sb.append(String.format("  * Successfully Allocated:      %d requests\n", allocatedCount));
        sb.append(String.format("  * Waitlisted (Schedule/Cap):   %d requests\n", waitlistedCount));
        sb.append(String.format("  * Rejected (Policy Violation): %d requests\n", rejectedCount));
        sb.append(String.format("  * Allocation Success Rate:     %.1f%%\n", efficiency));
        sb.append(String.format("  * Est. Operational Cost:       $%.2f\n", totalCost));
        sb.append(subline).append("\n\n");

        sb.append("APPROVED CAMPUS ALLOCATIONS:\n");
        sb.append(String.format("%-10s | %-28s | %-24s | %-10s\n", "Alloc ID", "Resource Allocated", "Requester", "Est. Cost"));
        sb.append(subline).append("\n");
        if (allocations.isEmpty()) {
            sb.append("  (No active allocations yet. Run the simulation engine to process requests.)\n");
        } else {
            for (Allocation a : allocations) {
                sb.append(String.format("%-10s | %-28s | %-24s | $%.2f\n",
                        a.getAllocationId(),
                        a.getResource().getName(),
                        a.getRequest().getRequester().getName(),
                        a.getTotalEstimatedCost()));
                sb.append("   Time Window: ").append(a.getTimeSlot().getFormatted()).append("\n");
            }
        }
        sb.append("\n");

        sb.append("WAITLISTED REQUESTS & CONFLICT ANALYSIS:\n");
        sb.append(subline).append("\n");
        boolean hasConflicts = false;
        for (AllocationRequest r : requests) {
            if (r.getStatus() == RequestStatus.WAITLISTED || r.getStatus() == RequestStatus.REJECTED) {
                hasConflicts = true;
                sb.append(String.format("[%s] %s by %s: %s\n",
                        r.getRequestId(),
                        r.getRequestedType().getDisplayName(),
                        r.getRequester().getName(),
                        r.getStatus()));
                sb.append("   Reason: ").append(r.getDecisionExplanation()).append("\n\n");
            }
        }
        if (!hasConflicts) {
            sb.append("  (No waitlisted requests or conflicts recorded.)\n");
        }

        sb.append(line).append("\n");
        sb.append("                     END OF CAMPUS AUDIT REPORT                                 \n");
        sb.append(line).append("\n");

        reportTextArea.setText(sb.toString());
        reportTextArea.setCaretPosition(0);
    }
}
