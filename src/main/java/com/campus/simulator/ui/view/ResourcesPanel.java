package com.campus.simulator.ui.view;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.ui.component.CardPanel;
import com.campus.simulator.ui.component.StatusBadgeRenderer;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.ResourceController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Screen for managing campus physical assets and inventory states.
 */
public class ResourcesPanel extends JPanel {

    private final ResourceController controller;

    private DefaultTableModel tableModel;
    private JTable resourcesTable;

    // Add Form Components
    private JComboBox<ResourceType> typeCombo;
    private JTextField nameField;
    private JTextField locationField;
    private JSpinner capacitySpinner;
    private JTextField extraAttr1Field;
    private JTextField extraAttr2Field;
    private JCheckBox extraCheck1;
    private JCheckBox extraCheck2;
    private JLabel extraAttr1Label;
    private JLabel extraAttr2Label;

    public ResourcesPanel(ResourceController controller) {
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

        JLabel title = new JLabel("Campus Resources Inventory");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Inspect facilities, adjust operational maintenance status, or register new spaces");
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

        // Left: Resource List & Action Buttons
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel tableCard = new CardPanel("Registered Resources");
        tableCard.setLayout(new BorderLayout(0, 8));

        String[] cols = {"ID", "Name", "Type", "Location", "Cap", "Status", "Attributes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        resourcesTable = new JTable(tableModel);
        UITheme.styleTable(resourcesTable);
        resourcesTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        resourcesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        resourcesTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        resourcesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        resourcesTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        resourcesTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        resourcesTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        resourcesTable.getColumnModel().getColumn(6).setPreferredWidth(210);

        JScrollPane scrollPane = new JScrollPane(resourcesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Table Action Buttons
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionsBar.setBackground(UITheme.COLOR_APP_BG);

        JButton markMaintBtn = UITheme.createSecondaryButton("Mark Maintenance");
        markMaintBtn.addActionListener(e -> onMarkMaintenance());

        JButton markAvailBtn = UITheme.createSecondaryButton("Mark Available");
        markAvailBtn.addActionListener(e -> onMarkAvailable());

        JButton removeBtn = UITheme.createSecondaryButton("Remove Resource");
        removeBtn.addActionListener(e -> onRemoveResource());

        actionsBar.add(markMaintBtn);
        actionsBar.add(markAvailBtn);
        actionsBar.add(removeBtn);

        leftPanel.add(tableCard, BorderLayout.CENTER);
        leftPanel.add(actionsBar, BorderLayout.SOUTH);

        // Right: Add Resource Form
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel formCard = new CardPanel("Add New Resource");
        formCard.setLayout(new BorderLayout(0, 12));

        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(UITheme.COLOR_CARD_BG);

        typeCombo = new JComboBox<>(ResourceType.values());
        typeCombo.addActionListener(e -> updateDynamicFormLabels());

        nameField = new JTextField(15);
        locationField = new JTextField(15);
        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 5000, 5));

        extraAttr1Label = new JLabel("Room Number:");
        extraAttr1Field = new JTextField(15);
        extraAttr2Label = new JLabel("Safety Level:");
        extraAttr2Field = new JTextField(15);

        extraCheck1 = new JCheckBox("Whiteboard Available", true);
        extraCheck1.setBackground(UITheme.COLOR_CARD_BG);
        extraCheck2 = new JCheckBox("Projection Screen Available", true);
        extraCheck2.setBackground(UITheme.COLOR_CARD_BG);

        addField(formFields, "Resource Type:", typeCombo);
        addField(formFields, "Name / Title:", nameField);
        addField(formFields, "Campus Location:", locationField);
        addField(formFields, "Capacity (Seats / Devices):", capacitySpinner);
        addField(formFields, extraAttr1Label, extraAttr1Field);
        addField(formFields, extraAttr2Label, extraAttr2Field);

        JPanel checkPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        checkPanel.setBackground(UITheme.COLOR_CARD_BG);
        checkPanel.add(extraCheck1);
        checkPanel.add(extraCheck2);
        formFields.add(checkPanel);

        JButton addBtn = UITheme.createPrimaryButton("Add Resource to Inventory");
        addBtn.addActionListener(e -> onAddResource());

        formCard.add(new JScrollPane(formFields), BorderLayout.CENTER);
        formCard.add(addBtn, BorderLayout.SOUTH);
        rightPanel.add(formCard, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        updateDynamicFormLabels();
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.bold(11.5f));
        label.setForeground(UITheme.COLOR_TEXT_SECONDARY);
        addField(panel, label, field);
    }

    private void addField(JPanel panel, JLabel label, JComponent field) {
        panel.add(label);
        panel.add(Box.createVerticalStrut(2));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    private void updateDynamicFormLabels() {
        ResourceType type = (ResourceType) typeCombo.getSelectedItem();
        if (type == null) return;

        switch (type) {
            case CLASSROOM -> {
                extraAttr1Label.setText("Room Number:");
                extraAttr1Field.setVisible(true);
                extraAttr2Label.setVisible(false);
                extraAttr2Field.setVisible(false);
                extraCheck1.setVisible(true);
                extraCheck1.setText("Whiteboard Available");
                extraCheck2.setVisible(true);
                extraCheck2.setText("Projector Screen Available");
            }
            case LABORATORY -> {
                extraAttr1Label.setText("Lab Specialization:");
                extraAttr1Field.setVisible(true);
                extraAttr2Label.setVisible(true);
                extraAttr2Label.setText("Safety Level (e.g. General, BSL-2):");
                extraAttr2Field.setVisible(true);
                extraCheck1.setVisible(false);
                extraCheck2.setVisible(false);
            }
            case PROJECTOR -> {
                extraAttr1Label.setText("Resolution (e.g. 4K UHD, 1080p):");
                extraAttr1Field.setVisible(true);
                extraAttr2Label.setText("Storage / Docking Bay:");
                extraAttr2Field.setVisible(true);
                extraAttr2Label.setVisible(true);
                extraCheck1.setVisible(true);
                extraCheck1.setText("Is Portable Unit");
                extraCheck2.setVisible(false);
            }
            case LAPTOP_SET -> {
                extraAttr1Label.setText("Operating System:");
                extraAttr1Field.setVisible(true);
                extraAttr2Label.setText("Storage Locker:");
                extraAttr2Field.setVisible(true);
                extraAttr2Label.setVisible(true);
                extraCheck1.setVisible(false);
                extraCheck2.setVisible(false);
            }
            case SPORTS_FACILITY -> {
                extraAttr1Label.setText("Sport Type (e.g. Tennis, Soccer):");
                extraAttr1Field.setVisible(true);
                extraAttr2Label.setVisible(false);
                extraAttr2Field.setVisible(false);
                extraCheck1.setVisible(true);
                extraCheck1.setText("Outdoor Facility");
                extraCheck2.setVisible(false);
            }
            case STUDY_ROOM -> {
                extraAttr1Label.setVisible(false);
                extraAttr1Field.setVisible(false);
                extraAttr2Label.setVisible(false);
                extraAttr2Field.setVisible(false);
                extraCheck1.setVisible(true);
                extraCheck1.setText("Quiet Zone Enforcement");
                extraCheck2.setVisible(true);
                extraCheck2.setText("External Monitor Installed");
            }
        }
        revalidate();
        repaint();
    }

    private void onAddResource() {
        try {
            ResourceType type = (ResourceType) typeCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            int capacity = (Integer) capacitySpinner.getValue();

            Map<String, Object> attrs = new HashMap<>();
            if (type == ResourceType.CLASSROOM) {
                attrs.put("roomNumber", extraAttr1Field.getText().trim());
                attrs.put("hasWhiteboard", extraCheck1.isSelected());
                attrs.put("hasProjectorScreen", extraCheck2.isSelected());
            } else if (type == ResourceType.LABORATORY) {
                attrs.put("labType", extraAttr1Field.getText().trim());
                attrs.put("safetyLevel", extraAttr2Field.getText().trim());
            } else if (type == ResourceType.PROJECTOR) {
                attrs.put("resolution", extraAttr1Field.getText().trim());
                attrs.put("portable", extraCheck1.isSelected());
                attrs.put("storageLocation", extraAttr2Field.getText().trim());
            } else if (type == ResourceType.LAPTOP_SET) {
                attrs.put("operatingSystem", extraAttr1Field.getText().trim());
                attrs.put("storageLocation", extraAttr2Field.getText().trim());
            } else if (type == ResourceType.SPORTS_FACILITY) {
                attrs.put("sportType", extraAttr1Field.getText().trim());
                attrs.put("outdoor", extraCheck1.isSelected());
            } else if (type == ResourceType.STUDY_ROOM) {
                attrs.put("quietZone", extraCheck1.isSelected());
                attrs.put("hasMonitor", extraCheck2.isSelected());
            }

            controller.addResource(type, name, location, capacity, attrs);

            nameField.setText("");
            locationField.setText("");
            extraAttr1Field.setText("");
            extraAttr2Field.setText("");

            JOptionPane.showMessageDialog(this, "Resource successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onMarkMaintenance() {
        int row = resourcesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a resource from the table first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        String reason = JOptionPane.showInputDialog(this, "Enter reason for maintenance servicing:", "Scheduled Maintenance", JOptionPane.QUESTION_MESSAGE);
        if (reason != null && !reason.trim().isEmpty()) {
            try {
                controller.markMaintenance(id, reason.trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onMarkAvailable() {
        int row = resourcesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a resource from the table first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            controller.markAvailable(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemoveResource() {
        int row = resourcesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a resource to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete resource " + id + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeResource(id);
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Resource> resources = controller.getAllResources();
        for (Resource r : resources) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getName(),
                    r.getResourceType().getDisplayName(),
                    r.getLocation(),
                    r.getCapacity(),
                    r.getStatus(),
                    r.getSpecificDetailsSummary()
            });
        }
    }
}
