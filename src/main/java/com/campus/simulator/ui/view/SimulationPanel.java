package com.campus.simulator.ui.view;

import com.campus.simulator.model.allocation.Allocation;
import com.campus.simulator.model.allocation.AllocationOutcome;
import com.campus.simulator.model.allocation.AllocationResult;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.service.allocation.AllocationStrategyType;
import com.campus.simulator.ui.component.CardPanel;
import com.campus.simulator.ui.component.StatusBadgeRenderer;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.SimulationController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen executing and analyzing the core Allocation Simulation Engine.
 * Allows comparing Priority-First, Fairness-Aware, and Capacity-Optimized strategies.
 */
public class SimulationPanel extends JPanel {

    private final SimulationController controller;

    private JComboBox<AllocationStrategyType> strategyCombo;
    private JLabel strategyDescLabel;
    private JButton runBtn;
    private JButton resetBtn;
    private JProgressBar progressBar;
    private JLabel statusFeedbackLabel;

    private DefaultTableModel resultsTableModel;
    private JTable resultsTable;

    private DefaultListModel<String> conflictsListModel;
    private JList<String> conflictsList;
    private JTextArea explanationArea;

    private List<AllocationResult> lastSimulationResults = new ArrayList<>();

    public SimulationPanel(SimulationController controller) {
        this.controller = controller;
        this.controller.setOnDataChangedCallback(this::onModelReset);

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_APP_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initHeader();
        initControlsSection();
        initResultsAndDiagnostics();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_APP_BG);

        JLabel title = new JLabel("Resource Allocation Simulation Engine");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Execute automated resolution rules across competing departmental and event resource demands");
        subtitle.setFont(UITheme.regular(12.5f));
        subtitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initControlsSection() {
        CardPanel controlCard = new CardPanel();
        controlCard.setLayout(new BorderLayout(12, 10));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        topRow.setBackground(UITheme.COLOR_CARD_BG);

        JLabel strategyLbl = new JLabel("Select Allocation Strategy:");
        strategyLbl.setFont(UITheme.bold(12.5f));
        strategyLbl.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        strategyCombo = new JComboBox<>(AllocationStrategyType.values());
        strategyCombo.setFont(UITheme.regular(12.5f));
        strategyCombo.setPreferredSize(new Dimension(280, 30));
        strategyCombo.addActionListener(e -> updateStrategyDescription());

        runBtn = UITheme.createPrimaryButton("Run Allocation Simulation");
        runBtn.addActionListener(e -> onRunSimulation());

        resetBtn = UITheme.createSecondaryButton("Reset Simulation with Seed Data");
        resetBtn.addActionListener(e -> onResetSimulation());

        JButton releaseBtn = UITheme.createSecondaryButton("Release Selected Allocation");
        releaseBtn.addActionListener(e -> onReleaseSelectedAllocation());

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(100, 20));

        topRow.add(strategyLbl);
        topRow.add(strategyCombo);
        topRow.add(runBtn);
        topRow.add(resetBtn);
        topRow.add(releaseBtn);
        topRow.add(progressBar);

        strategyDescLabel = new JLabel();
        strategyDescLabel.setFont(UITheme.regular(12.0f));
        strategyDescLabel.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        strategyDescLabel.setBorder(new EmptyBorder(2, 6, 2, 6));

        statusFeedbackLabel = new JLabel("Ready to simulate. Select a strategy and click 'Run Allocation Simulation'.");
        statusFeedbackLabel.setFont(UITheme.bold(12.0f));
        statusFeedbackLabel.setForeground(UITheme.COLOR_ACCENT_BLUE);
        statusFeedbackLabel.setBorder(new EmptyBorder(2, 6, 2, 6));

        JPanel bottomRow = new JPanel(new GridLayout(2, 1, 4, 4));
        bottomRow.setBackground(UITheme.COLOR_CARD_BG);
        bottomRow.add(strategyDescLabel);
        bottomRow.add(statusFeedbackLabel);

        controlCard.add(topRow, BorderLayout.NORTH);
        controlCard.add(bottomRow, BorderLayout.CENTER);

        updateStrategyDescription();

        add(controlCard, BorderLayout.BEFORE_FIRST_LINE); // handled by compound container
    }

    private void updateStrategyDescription() {
        AllocationStrategyType type = (AllocationStrategyType) strategyCombo.getSelectedItem();
        if (type != null) {
            strategyDescLabel.setText("Strategy Logic: " + type.getDescription());
        }
    }

    private void initResultsAndDiagnostics() {
        JPanel centerContainer = new JPanel(new BorderLayout(0, 14));
        centerContainer.setBackground(UITheme.COLOR_APP_BG);

        // Put control card inside centerContainer top
        CardPanel controlCard = new CardPanel();
        controlCard.setLayout(new BorderLayout(12, 10));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        topRow.setBackground(UITheme.COLOR_CARD_BG);

        JLabel strategyLbl = new JLabel("Allocation Strategy:");
        strategyLbl.setFont(UITheme.bold(12.5f));
        strategyLbl.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        topRow.add(strategyLbl);
        topRow.add(strategyCombo);
        topRow.add(runBtn);
        topRow.add(resetBtn);

        JButton releaseBtn = UITheme.createSecondaryButton("Release Selected Allocation");
        releaseBtn.addActionListener(e -> onReleaseSelectedAllocation());
        topRow.add(releaseBtn);
        topRow.add(progressBar);

        JPanel bottomRow = new JPanel(new GridLayout(2, 1, 4, 4));
        bottomRow.setBackground(UITheme.COLOR_CARD_BG);
        bottomRow.add(strategyDescLabel);
        bottomRow.add(statusFeedbackLabel);

        controlCard.add(topRow, BorderLayout.NORTH);
        controlCard.add(bottomRow, BorderLayout.CENTER);
        centerContainer.add(controlCard, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setBackground(UITheme.COLOR_APP_BG);

        // Top: Simulation Results Table
        CardPanel tableCard = new CardPanel("Simulation Decisions & Allocations");
        tableCard.setLayout(new BorderLayout(0, 8));

        String[] cols = {"Request ID", "Requester", "Resource Allocated", "Time Slot", "Outcome", "Decision Explanation"};
        resultsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        resultsTable = new JTable(resultsTableModel);
        UITheme.styleTable(resultsTable);
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(75);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(300);

        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane scrollTable = new JScrollPane(resultsTable);
        scrollTable.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        tableCard.add(scrollTable, BorderLayout.CENTER);
        splitPane.setTopComponent(tableCard);

        // Bottom: Diagnostics & Conflict Inspector
        JPanel diagnosticsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        diagnosticsPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel conflictsCard = new CardPanel("Schedule Collisions & Evaluated Candidates");
        conflictsListModel = new DefaultListModel<>();
        conflictsList = new JList<>(conflictsListModel);
        conflictsList.setFont(UITheme.regular(12.0f));
        conflictsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollConflicts = new JScrollPane(conflictsList);
        scrollConflicts.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        conflictsCard.add(scrollConflicts, BorderLayout.CENTER);

        CardPanel explanationCard = new CardPanel("Selected Request Detailed Audit Log");
        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setFont(UITheme.regular(12.0f));
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setBackground(UITheme.COLOR_CARD_BG);
        explanationArea.setText("Select a row in the results table above to inspect candidate evaluations and collision diagnostics.");
        JScrollPane scrollExp = new JScrollPane(explanationArea);
        scrollExp.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        explanationCard.add(scrollExp, BorderLayout.CENTER);

        diagnosticsPanel.add(conflictsCard);
        diagnosticsPanel.add(explanationCard);

        splitPane.setBottomComponent(diagnosticsPanel);
        centerContainer.add(splitPane, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
    }

    private void onRunSimulation() {
        AllocationStrategyType selectedStrategy = (AllocationStrategyType) strategyCombo.getSelectedItem();
        if (selectedStrategy == null) return;

        controller.runSimulationAsync(
                selectedStrategy,
                () -> {
                    runBtn.setEnabled(false);
                    resetBtn.setEnabled(false);
                    progressBar.setVisible(true);
                    statusFeedbackLabel.setText("Running simulation using " + selectedStrategy.getDisplayName() + "...");
                },
                results -> {
                    runBtn.setEnabled(true);
                    resetBtn.setEnabled(true);
                    progressBar.setVisible(false);
                    this.lastSimulationResults = results;
                    renderResults(results);
                    long approved = results.stream().filter(AllocationResult::isApproved).count();
                    long waitlisted = results.stream().filter(AllocationResult::isWaitlisted).count();
                    long rejected = results.stream().filter(AllocationResult::isRejected).count();
                    statusFeedbackLabel.setText(String.format(
                            "Simulation Completed (%s): %d Approved, %d Waitlisted, %d Rejected",
                            selectedStrategy.getDisplayName(), approved, waitlisted, rejected));
                },
                error -> {
                    runBtn.setEnabled(true);
                    resetBtn.setEnabled(true);
                    progressBar.setVisible(false);
                    statusFeedbackLabel.setText("Simulation failed: " + error.getMessage());
                    JOptionPane.showMessageDialog(this, error.getMessage(), "Simulation Error", JOptionPane.ERROR_MESSAGE);
                }
        );
    }

    private void renderResults(List<AllocationResult> results) {
        resultsTableModel.setRowCount(0);
        conflictsListModel.clear();
        explanationArea.setText("Select a row in the results table to view in-depth resolution breakdown.");

        for (AllocationResult res : results) {
            String allocatedResourceName = res.getAllocation()
                    .map(a -> a.getResource().getName() + " (Cap: " + a.getResource().getCapacity() + ")")
                    .orElse("None");

            resultsTableModel.addRow(new Object[]{
                    res.getRequest().getId(),
                    res.getRequest().getRequester().getName(),
                    allocatedResourceName,
                    res.getRequest().getRequestedTimeSlot().getFormatted(),
                    res.getOutcome(),
                    res.getExplanation()
            });
        }
    }

    private void onTableRowSelected() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= lastSimulationResults.size()) {
            return;
        }

        AllocationResult result = lastSimulationResults.get(selectedRow);
        conflictsListModel.clear();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ALLOCATION AUDIT LOG ===\n");
        sb.append("Request ID: ").append(result.getRequest().getId()).append("\n");
        sb.append("Requester: ").append(result.getRequest().getRequester().getName())
                .append(" (").append(result.getRequest().getRequester().getRequesterType().getDisplayName()).append(")\n");
        sb.append("Priority Tier: ").append(result.getRequest().getRequester().getPriorityTier())
                .append(" | Request Urgency: ").append(result.getRequest().getPriority()).append("\n");
        sb.append("Calculated Priority Score: ").append(result.getRequest().calculateEffectivePriority()).append("\n");
        sb.append("Requested Type: ").append(result.getRequest().getResourceType().getDisplayName()).append("\n");
        sb.append("Required Capacity: ").append(result.getRequest().getRequiredCapacity()).append("\n");
        sb.append("Requested Time Slot: ").append(result.getRequest().getRequestedTimeSlot().getFormatted()).append("\n");
        sb.append("Outcome: ").append(result.getOutcome()).append("\n");
        sb.append("Summary: ").append(result.getExplanation()).append("\n\n");

        if (result.getAllocation().isPresent()) {
            Allocation a = result.getAllocation().get();
            sb.append("Assigned Asset: ").append(a.getResource().getName()).append("\n");
            sb.append("Location: ").append(a.getResource().getLocation()).append("\n");
            sb.append("Capacity: ").append(a.getResource().getCapacity()).append("\n");
            sb.append("Features: ").append(a.getResource().getSpecificDetailsSummary()).append("\n");
        }

        explanationArea.setText(sb.toString());

        if (result.getConsideredResources().isEmpty()) {
            conflictsListModel.addElement("No candidate resources evaluated");
        } else {
            conflictsListModel.addElement("--- Evaluated Candidate Resources ---");
            for (Resource r : result.getConsideredResources()) {
                conflictsListModel.addElement(String.format("[%s] %s (Cap: %d, %s)",
                        r.getId(), r.getName(), r.getCapacity(), r.getStatus()));
            }
        }

        if (!result.getConflictDetails().isEmpty()) {
            conflictsListModel.addElement("--- Collision & Diagnostic Details ---");
            for (String conflict : result.getConflictDetails()) {
                conflictsListModel.addElement("• " + conflict);
            }
        } else if (result.isApproved()) {
            conflictsListModel.addElement("• Zero collisions; allocation completed seamlessly.");
        }
    }

    private void onResetSimulation() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset simulation state and re-seed with baseline demo dataset?",
                "Confirm Simulation Reset",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.resetSimulation();
            resultsTableModel.setRowCount(0);
            conflictsListModel.clear();
            explanationArea.setText("Simulation environment restored to baseline dataset. Ready to execute.");
            statusFeedbackLabel.setText("Simulation reset with seed data.");
        }
    }

    private void onReleaseSelectedAllocation() {
        int row = resultsTable.getSelectedRow();
        if (row < 0 || row >= lastSimulationResults.size()) {
            JOptionPane.showMessageDialog(this, "Please select an approved allocation row in the table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AllocationResult res = lastSimulationResults.get(row);
        if (!res.isApproved() || res.getAllocation().isEmpty()) {
            JOptionPane.showMessageDialog(this, "The selected row is not an approved allocation.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String allocationId = res.getAllocation().get().getId();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Release allocation " + allocationId + " ahead of schedule?",
                "Confirm Early Release",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.releaseAllocation(allocationId, "Released early by campus dispatcher");
                JOptionPane.showMessageDialog(this, "Allocation " + allocationId + " released successfully!", "Released", JOptionPane.INFORMATION_MESSAGE);
                onRunSimulation(); // Refresh simulation table
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onModelReset() {
        resultsTableModel.setRowCount(0);
        conflictsListModel.clear();
        explanationArea.setText("Ready to simulate. Select a strategy and click 'Run Allocation Simulation'.");
    }
}
