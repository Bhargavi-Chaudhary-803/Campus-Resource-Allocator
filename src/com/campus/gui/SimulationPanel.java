package com.campus.gui;

import com.campus.model.AllocationRequest;
import com.campus.service.CampusDatabase;
import com.campus.service.SimulationEngine;
import com.campus.strategy.AllocationStrategy;
import com.campus.strategy.CapacityStrategy;
import com.campus.strategy.FairnessStrategy;
import com.campus.strategy.PriorityStrategy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: SIMULATION ENGINE CONTROL & CONFLICT RESOLUTION
 * =========================================================================
 * Highlights:
 * - DESIGN PATTERN: STRATEGY PATTERN (Switching algorithms at runtime)
 * - JAVA CONCEPT: MULTITHREADING (SwingWorker background execution for smooth UI)
 * - Conflict detection & policy enforcement audit display
 */
public class SimulationPanel extends JPanel {

    private final CampusDatabase db;
    private final SimulationEngine engine;
    private final Runnable onSimulationCompleteCallback;

    private JComboBox<String> strategyCombo;
    private JTextArea strategyDescriptionArea;
    private JButton runButton;
    private JButton resetButton;
    private JProgressBar progressBar;

    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public SimulationPanel(CampusDatabase db, SimulationEngine engine, Runnable onSimulationCompleteCallback) {
        this.db = db;
        this.engine = engine;
        this.onSimulationCompleteCallback = onSimulationCompleteCallback;

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.OFF_WHITE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initComponents();
        updateStrategyDescription();
        refreshResults();
    }

    private void initComponents() {
        // --- 1. Top Panel: Strategy Configuration Card ---
        JPanel topContainer = new JPanel(new BorderLayout(0, 14));
        topContainer.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Allocation Engine & Strategy Simulator");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Execute dynamic campus allocation algorithms with schedule collision detection and fairness balancing.");
        subtitle.setFont(UITheme.regular(12.0f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        topContainer.add(titlePanel, BorderLayout.NORTH);

        // Strategy Control Card
        JPanel controlCard = new JPanel(new BorderLayout(16, 12));
        controlCard.setBackground(UITheme.CARD_BG);
        controlCard.setBorder(UITheme.createCardBorder());

        JPanel controlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        controlTop.setOpaque(false);

        JLabel strategyLbl = new JLabel("Active Allocation Strategy:");
        strategyLbl.setFont(UITheme.bold(12.0f));
        strategyLbl.setForeground(UITheme.TEXT_PRIMARY);

        strategyCombo = new JComboBox<>(new String[]{
                "1. Priority-First Strategy (Institutional Tier + Urgency)",
                "2. Fairness-Aware Strategy (Penalizes Heavy Requesters)",
                "3. Capacity-Optimized Strategy (Best-Fit Seat Minimizer)"
        });
        strategyCombo.setBackground(Color.WHITE);
        strategyCombo.setPreferredSize(new Dimension(380, 30));
        strategyCombo.addActionListener(e -> onStrategyChanged());

        runButton = UITheme.createPrimaryButton("▶ Run Simulation Engine");
        runButton.addActionListener(e -> executeSimulationWorker());

        resetButton = UITheme.createSecondaryButton("Reset Simulation");
        resetButton.addActionListener(e -> resetSimulation());

        controlTop.add(strategyLbl);
        controlTop.add(strategyCombo);
        controlTop.add(Box.createHorizontalStrut(10));
        controlTop.add(runButton);
        controlTop.add(resetButton);

        controlCard.add(controlTop, BorderLayout.NORTH);

        // Strategy Explanation Text Area
        strategyDescriptionArea = new JTextArea(3, 60);
        strategyDescriptionArea.setFont(UITheme.regular(12.0f));
        strategyDescriptionArea.setForeground(UITheme.TEXT_SECONDARY);
        strategyDescriptionArea.setEditable(false);
        strategyDescriptionArea.setLineWrap(true);
        strategyDescriptionArea.setWrapStyleWord(true);
        strategyDescriptionArea.setBackground(new Color(248, 250, 252));
        strategyDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        controlCard.add(strategyDescriptionArea, BorderLayout.CENTER);

        // Multithreading Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready to simulate");
        progressBar.setFont(UITheme.bold(11.0f));
        progressBar.setForeground(UITheme.PRIMARY_BLUE);
        progressBar.setVisible(false);
        controlCard.add(progressBar, BorderLayout.SOUTH);

        topContainer.add(controlCard, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // --- 2. Center: Simulation Results Table Card ---
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.createCardBorder());

        JLabel resultsTitle = new JLabel("Simulation Outcomes & Conflict Explanation Log");
        resultsTitle.setFont(UITheme.bold(14.0f));
        resultsTitle.setForeground(UITheme.TEXT_PRIMARY);
        tableCard.add(resultsTitle, BorderLayout.NORTH);

        String[] columns = {"Request ID", "Requester", "Target Facility", "Requested Slot", "Seats", "Outcome", "Detailed Allocation or Conflict Reason"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        UITheme.styleTable(resultsTable);
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(UITheme.createStatusRenderer());

        // Adjust column widths for better readability
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(350);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void onStrategyChanged() {
        int index = strategyCombo.getSelectedIndex();
        AllocationStrategy strategy;
        if (index == 0) {
            strategy = new PriorityStrategy();
        } else if (index == 1) {
            strategy = new FairnessStrategy();
        } else {
            strategy = new CapacityStrategy();
        }

        // [DESIGN PATTERN: STRATEGY]: Dynamically injected into engine at runtime
        engine.setStrategy(strategy);
        updateStrategyDescription();
    }

    private void updateStrategyDescription() {
        AllocationStrategy current = engine.getCurrentStrategy();
        strategyDescriptionArea.setText("Algorithm: " + current.getName() + "\n" + current.getDescription());
    }

    /**
     * [JAVA CONCEPT - MULTITHREADING]: Executes allocation simulation in a background
     * SwingWorker thread, keeping the Swing UI responsive and animating a progress bar.
     */
    private void executeSimulationWorker() {
        runButton.setEnabled(false);
        resetButton.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        progressBar.setString("Running allocation engine on background thread...");

        SwingWorker<List<AllocationRequest>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AllocationRequest> doInBackground() throws Exception {
                // Simulate slight computation time for educational demonstration
                Thread.sleep(600);
                return engine.runSimulation();
            }

            @Override
            protected void done() {
                try {
                    List<AllocationRequest> results = get();
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Simulation completed successfully (" + results.size() + " requests evaluated)");
                    refreshResults();

                    if (onSimulationCompleteCallback != null) {
                        onSimulationCompleteCallback.run();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SimulationPanel.this, "Simulation error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    runButton.setEnabled(true);
                    resetButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void resetSimulation() {
        db.clearAllocations();
        progressBar.setValue(0);
        progressBar.setString("Simulation reset. Allocations cleared.");
        refreshResults();
        if (onSimulationCompleteCallback != null) {
            onSimulationCompleteCallback.run();
        }
    }

    public void refreshResults() {
        tableModel.setRowCount(0);
        List<AllocationRequest> requests = db.getRequests();

        for (AllocationRequest r : requests) {
            tableModel.addRow(new Object[]{
                    r.getRequestId(),
                    r.getRequester().getName(),
                    r.getRequestedType().getDisplayName(),
                    r.getTimeSlot().getFormatted(),
                    r.getRequiredCapacity(),
                    r.getStatus().name(),
                    r.getDecisionExplanation() == null ? "Pending execution" : r.getDecisionExplanation()
            });
        }
    }
}
