package com.campus.simulator.ui.view;

import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.ui.component.CardPanel;
import com.campus.simulator.ui.component.StatusBadgeRenderer;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.RequestController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Screen for submitting allocation requests, browsing pending bookings,
 * and reviewing rejection or waitlist reasons.
 */
public class RequestsPanel extends JPanel {

    private final RequestController controller;

    private DefaultTableModel tableModel;
    private JTable requestsTable;

    // Submission Form Components
    private JComboBox<RequesterItem> requesterCombo;
    private JComboBox<ResourceType> resourceTypeCombo;
    private JComboBox<ResourceItem> targetResourceCombo;
    private JSpinner dateOffsetSpinner; // Days from today
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JSpinner durationSpinner;
    private JSpinner capacitySpinner;
    private JComboBox<RequestPriority> priorityCombo;
    private JTextField purposeField;

    public RequestsPanel(RequestController controller) {
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

        JLabel title = new JLabel("Resource Allocation Requests");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Submit scheduling requirements, specify capacity needs, and inspect outcome diagnostics");
        subtitle.setFont(UITheme.regular(12.5f));
        subtitle.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.68);
        splitPane.setBorder(null);
        splitPane.setBackground(UITheme.COLOR_APP_BG);

        // Left Panel: Requests Table & Actions
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel tableCard = new CardPanel("Submitted Allocation Requests");
        tableCard.setLayout(new BorderLayout(0, 8));

        String[] cols = {"ID", "Requester", "Target Type", "Specific Resource", "Time Slot", "Cap", "Priority", "Status", "Reason / Diagnostics"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        requestsTable = new JTable(tableModel);
        UITheme.styleTable(requestsTable);
        requestsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        requestsTable.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());

        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(95);
        requestsTable.getColumnModel().getColumn(3).setPreferredWidth(95);
        requestsTable.getColumnModel().getColumn(4).setPreferredWidth(185);
        requestsTable.getColumnModel().getColumn(5).setPreferredWidth(45);
        requestsTable.getColumnModel().getColumn(6).setPreferredWidth(75);
        requestsTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        requestsTable.getColumnModel().getColumn(8).setPreferredWidth(210);

        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionsBar.setBackground(UITheme.COLOR_APP_BG);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel Selected Request");
        cancelBtn.addActionListener(e -> onCancelRequest());

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Table");
        refreshBtn.addActionListener(e -> refreshData());

        actionsBar.add(cancelBtn);
        actionsBar.add(refreshBtn);

        leftPanel.add(tableCard, BorderLayout.CENTER);
        leftPanel.add(actionsBar, BorderLayout.SOUTH);

        // Right Panel: Submit Request Form
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel formCard = new CardPanel("Submit New Request");
        formCard.setLayout(new BorderLayout(0, 12));

        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(UITheme.COLOR_CARD_BG);

        requesterCombo = new JComboBox<>();
        resourceTypeCombo = new JComboBox<>(ResourceType.values());
        resourceTypeCombo.addActionListener(e -> updateTargetResourceCombo());
        targetResourceCombo = new JComboBox<>();

        dateOffsetSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 60, 1));

        hourSpinner = new JSpinner(new SpinnerNumberModel(9, 6, 22, 1));
        minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 45, 15));
        durationSpinner = new JSpinner(new SpinnerNumberModel(120, 30, 480, 30));
        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 5));
        priorityCombo = new JComboBox<>(RequestPriority.values());
        priorityCombo.setSelectedItem(RequestPriority.NORMAL);
        purposeField = new JTextField(15);
        purposeField.setText("Academic Workshop");

        addField(formFields, "Requester Entity:", requesterCombo);
        addField(formFields, "Required Resource Type:", resourceTypeCombo);
        addField(formFields, "Optional Target Resource:", targetResourceCombo);

        JPanel timeGrid = new JPanel(new GridLayout(2, 2, 8, 4));
        timeGrid.setBackground(UITheme.COLOR_CARD_BG);

        timeGrid.add(createSubLabel("Days from today:"));
        timeGrid.add(createSubLabel("Start Hour (0-23):"));
        timeGrid.add(dateOffsetSpinner);
        timeGrid.add(hourSpinner);
        formFields.add(timeGrid);
        formFields.add(Box.createVerticalStrut(8));

        JPanel durationGrid = new JPanel(new GridLayout(2, 2, 8, 4));
        durationGrid.setBackground(UITheme.COLOR_CARD_BG);
        durationGrid.add(createSubLabel("Duration (Minutes):"));
        durationGrid.add(createSubLabel("Required Seats/Cap:"));
        durationGrid.add(durationSpinner);
        durationGrid.add(capacitySpinner);
        formFields.add(durationGrid);
        formFields.add(Box.createVerticalStrut(8));

        addField(formFields, "Urgency Priority:", priorityCombo);
        addField(formFields, "Purpose / Event Title:", purposeField);

        JButton submitBtn = UITheme.createPrimaryButton("Submit Request");
        submitBtn.addActionListener(e -> onSubmitRequest());

        formCard.add(new JScrollPane(formFields), BorderLayout.CENTER);
        formCard.add(submitBtn, BorderLayout.SOUTH);
        rightPanel.add(formCard, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel createSubLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.regular(11.0f));
        l.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        return l;
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.bold(11.5f));
        label.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        panel.add(label);
        panel.add(Box.createVerticalStrut(2));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(8));
    }

    private void updateTargetResourceCombo() {
        ResourceType selectedType = (ResourceType) resourceTypeCombo.getSelectedItem();
        targetResourceCombo.removeAllItems();
        targetResourceCombo.addItem(new ResourceItem(null, "(Any Compatible Resource)"));

        if (selectedType != null) {
            for (Resource r : controller.getAllResources()) {
                if (r.getResourceType() == selectedType) {
                    targetResourceCombo.addItem(new ResourceItem(r.getId(), r.getId() + " - " + r.getName()));
                }
            }
        }
    }

    private void onSubmitRequest() {
        try {
            RequesterItem reqItem = (RequesterItem) requesterCombo.getSelectedItem();
            if (reqItem == null || reqItem.requester() == null) {
                throw new IllegalArgumentException("Please select a requester");
            }

            ResourceType resType = (ResourceType) resourceTypeCombo.getSelectedItem();
            ResourceItem targetItem = (ResourceItem) targetResourceCombo.getSelectedItem();
            String targetId = targetItem != null ? targetItem.id() : null;

            int daysOffset = (Integer) dateOffsetSpinner.getValue();
            int hour = (Integer) hourSpinner.getValue();
            int minute = (Integer) minuteSpinner.getValue();
            int duration = (Integer) durationSpinner.getValue();
            int capacity = (Integer) capacitySpinner.getValue();
            RequestPriority priority = (RequestPriority) priorityCombo.getSelectedItem();
            String purpose = purposeField.getText().trim();

            LocalDateTime start = LocalDateTime.of(
                    LocalDate.now().plusDays(daysOffset),
                    LocalTime.of(hour, minute)
            );
            TimeSlot slot = TimeSlot.of(start, duration);

            controller.submitRequest(
                    reqItem.requester().getId(),
                    resType,
                    targetId,
                    slot,
                    capacity,
                    priority,
                    purpose
            );

            JOptionPane.showMessageDialog(this, "Request submitted to pending pool!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancelRequest() {
        int row = requestsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request from the table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        String reason = JOptionPane.showInputDialog(this, "Enter reason for cancellation:", "Cancel Request", JOptionPane.QUESTION_MESSAGE);
        if (reason != null && !reason.trim().isEmpty()) {
            try {
                controller.cancelRequest(id, reason.trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshData() {
        // Refresh Requester combobox
        RequesterItem currentReq = (RequesterItem) requesterCombo.getSelectedItem();
        requesterCombo.removeAllItems();
        for (Requester r : controller.getActiveRequesters()) {
            RequesterItem item = new RequesterItem(r);
            requesterCombo.addItem(item);
            if (currentReq != null && currentReq.requester().getId().equals(r.getId())) {
                requesterCombo.setSelectedItem(item);
            }
        }

        updateTargetResourceCombo();

        // Refresh table
        tableModel.setRowCount(0);
        List<ResourceRequest> requests = controller.getAllRequests();
        for (ResourceRequest req : requests) {
            tableModel.addRow(new Object[]{
                    req.getId(),
                    req.getRequester().getName(),
                    req.getResourceType().getDisplayName(),
                    req.getTargetResourceId().orElse("-"),
                    req.getRequestedTimeSlot().getFormatted(),
                    req.getRequiredCapacity(),
                    req.getPriority(),
                    req.getStatus(),
                    req.getStatusReason() != null ? req.getStatusReason() : "-"
            });
        }
    }

    private record RequesterItem(Requester requester) {
        @Override
        public String toString() {
            if (requester == null) return "Select Requester";
            return requester.getName() + " (" + requester.getRequesterType().getDisplayName() + ")";
        }
    }

    private record ResourceItem(String id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
