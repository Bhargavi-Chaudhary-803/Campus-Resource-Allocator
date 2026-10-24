package com.campus.gui;

import com.campus.model.*;
import com.campus.service.CampusDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: RESOURCE REQUEST QUEUE PANEL
 * =========================================================================
 * Demonstrates:
 * - Queue inspection of requests competing for limited campus assets
 * - Form submission creating AllocationRequest objects with domain validation
 * - Dynamic status tracking (PENDING, ALLOCATED, WAITLISTED, REJECTED)
 */
public class RequestPanel extends JPanel {

    private final CampusDatabase db;
    private JTable requestTable;
    private DefaultTableModel tableModel;

    public RequestPanel(CampusDatabase db) {
        this.db = db;
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.OFF_WHITE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- 1. Top Panel: Header and Actions ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Campus Allocation Request Queue");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Pending, scheduled, and conflicting facility booking requests submitted across academic departments.");
        subtitle.setFont(UITheme.regular(12.0f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setOpaque(false);

        JButton resetPendingBtn = UITheme.createSecondaryButton("Reset All to Pending");
        resetPendingBtn.addActionListener(e -> {
            db.clearAllocations();
            refreshData();
            JOptionPane.showMessageDialog(this, "All requests have been reset to PENDING status.", "Queue Reset", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton submitRequestBtn = UITheme.createPrimaryButton("+ Submit New Request");
        submitRequestBtn.addActionListener(e -> showSubmitRequestDialog());

        actions.add(resetPendingBtn);
        actions.add(submitRequestBtn);
        topPanel.add(actions, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center: Table Card ---
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.createCardBorder());

        String[] columns = {"Request ID", "Requester Entity", "Target Resource", "Time Slot", "Duration", "Seats Needed", "Priority", "Purpose / Event", "Status", "Resolution Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new JTable(tableModel);
        UITheme.styleTable(requestTable);
        requestTable.getColumnModel().getColumn(8).setCellRenderer(UITheme.createStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void showSubmitRequestDialog() {
        if (db.getRequesters().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please register at least one requester before submitting requests.", "No Requesters", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Submit Allocation Request", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 12));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JComboBox<Requester> requesterCombo = new JComboBox<>(db.getRequesters().toArray(new Requester[0]));
        requesterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Requester r) {
                    setText(r.getName() + " (" + r.getType().getDisplayName() + ")");
                }
                return this;
            }
        });

        JComboBox<ResourceType> typeCombo = new JComboBox<>(ResourceType.values());
        JSpinner daysAheadSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 30, 1));
        JSpinner startHourSpinner = new JSpinner(new SpinnerNumberModel(9, 8, 20, 1));
        JSpinner endHourSpinner = new JSpinner(new SpinnerNumberModel(11, 9, 21, 1));
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(40, 1, 500, 5));
        JComboBox<PriorityLevel> priorityCombo = new JComboBox<>(PriorityLevel.values());
        JTextField purposeField = new JTextField("Academic lecture / session");

        formPanel.add(new JLabel("Requesting Entity:"));
        formPanel.add(requesterCombo);
        formPanel.add(new JLabel("Resource Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Days in Future (0 = Today):"));
        formPanel.add(daysAheadSpinner);
        formPanel.add(new JLabel("Start Hour (24h, e.g. 9):"));
        formPanel.add(startHourSpinner);
        formPanel.add(new JLabel("End Hour (24h, e.g. 11):"));
        formPanel.add(endHourSpinner);
        formPanel.add(new JLabel("Required Capacity (Seats):"));
        formPanel.add(capacitySpinner);
        formPanel.add(new JLabel("Urgency / Priority:"));
        formPanel.add(priorityCombo);
        formPanel.add(new JLabel("Event Purpose / Title:"));
        formPanel.add(purposeField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(UITheme.OFF_WHITE_BG);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = UITheme.createPrimaryButton("Submit Request");
        submitBtn.addActionListener(e -> {
            int start = (int) startHourSpinner.getValue();
            int end = (int) endHourSpinner.getValue();

            if (end <= start) {
                JOptionPane.showMessageDialog(dialog, "End hour must be strictly greater than start hour.", "Invalid Time Slot", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int days = (int) daysAheadSpinner.getValue();
            LocalDate targetDate = LocalDate.now().plusDays(days);
            TimeSlot slot = new TimeSlot(targetDate, start, end);

            Requester requester = (Requester) requesterCombo.getSelectedItem();
            ResourceType resType = (ResourceType) typeCombo.getSelectedItem();
            int cap = (int) capacitySpinner.getValue();
            PriorityLevel prio = (PriorityLevel) priorityCombo.getSelectedItem();
            String purpose = purposeField.getText().trim();

            if (purpose.isEmpty()) purpose = "Standard campus reservation";

            // Add request to database
            db.submitRequest(requester, resType, null, slot, cap, prio, purpose);
            refreshData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Request submitted to queue! Run the simulation engine to process it.", "Request Created", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<AllocationRequest> list = db.getRequests();

        for (AllocationRequest r : list) {
            tableModel.addRow(new Object[]{
                    r.getRequestId(),
                    r.getRequester().getName(),
                    r.getRequestedType().getDisplayName(),
                    r.getTimeSlot().getFormatted(),
                    r.getTimeSlot().getDurationHours() + " hrs",
                    r.getRequiredCapacity() + " seats",
                    r.getRequestPriority().name(),
                    r.getPurpose(),
                    r.getStatus().name(),
                    r.getDecisionExplanation() == null ? "Pending simulation evaluation" : r.getDecisionExplanation()
            });
        }
    }
}
