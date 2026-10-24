package com.campus.simulator.ui.view;

import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.ui.component.CardPanel;
import com.campus.simulator.ui.component.StatusBadgeRenderer;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.RequesterController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Screen for managing university departments, faculty members, student clubs, and event organizers.
 */
public class RequestersPanel extends JPanel {

    private final RequesterController controller;

    private DefaultTableModel tableModel;
    private JTable requestersTable;

    // Add Form Components
    private JComboBox<RequesterType> typeCombo;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<PriorityTier> tierCombo;

    private JLabel attr1Label;
    private JTextField attr1Field;
    private JLabel attr2Label;
    private JTextField attr2Field;

    public RequestersPanel(RequesterController controller) {
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

        JLabel title = new JLabel("University Requesters Registry");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage academic departments, faculty profiles, registered student clubs, and event bodies");
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

        // Left: Requester Table & Actions
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel tableCard = new CardPanel("Registered University Requesters");
        tableCard.setLayout(new BorderLayout(0, 8));

        String[] cols = {"ID", "Name", "Type", "Contact Email", "Priority Tier", "Active", "Profile Info"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        requestersTable = new JTable(tableModel);
        UITheme.styleTable(requestersTable);
        requestersTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        requestersTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        requestersTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        requestersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        requestersTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        requestersTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        requestersTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        requestersTable.getColumnModel().getColumn(6).setPreferredWidth(210);

        JScrollPane scrollPane = new JScrollPane(requestersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionsBar.setBackground(UITheme.COLOR_APP_BG);

        JButton toggleActiveBtn = UITheme.createSecondaryButton("Toggle Active / Inactive");
        toggleActiveBtn.addActionListener(e -> onToggleActive());

        JButton removeBtn = UITheme.createSecondaryButton("Remove Requester");
        removeBtn.addActionListener(e -> onRemoveRequester());

        actionsBar.add(toggleActiveBtn);
        actionsBar.add(removeBtn);

        leftPanel.add(tableCard, BorderLayout.CENTER);
        leftPanel.add(actionsBar, BorderLayout.SOUTH);

        // Right: Add Requester Form
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(UITheme.COLOR_APP_BG);

        CardPanel formCard = new CardPanel("Enroll New Requester");
        formCard.setLayout(new BorderLayout(0, 12));

        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(UITheme.COLOR_CARD_BG);

        typeCombo = new JComboBox<>(RequesterType.values());
        typeCombo.addActionListener(e -> updateDynamicFormLabels());

        nameField = new JTextField(15);
        emailField = new JTextField(15);
        tierCombo = new JComboBox<>(PriorityTier.values());
        tierCombo.setSelectedItem(PriorityTier.MEDIUM);

        attr1Label = new JLabel("Department Code:");
        attr1Field = new JTextField(15);
        attr2Label = new JLabel("Faculty Chairperson:");
        attr2Field = new JTextField(15);

        addField(formFields, "Requester Classification:", typeCombo);
        addField(formFields, "Full Name / Organization:", nameField);
        addField(formFields, "Official Campus Email:", emailField);
        addField(formFields, "Institutional Priority Tier:", tierCombo);
        addField(formFields, attr1Label, attr1Field);
        addField(formFields, attr2Label, attr2Field);

        JButton addBtn = UITheme.createPrimaryButton("Enroll Requester");
        addBtn.addActionListener(e -> onAddRequester());

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
        RequesterType type = (RequesterType) typeCombo.getSelectedItem();
        if (type == null) return;

        switch (type) {
            case DEPARTMENT -> {
                attr1Label.setText("Department Code (e.g. CS-DEPT):");
                attr2Label.setText("Faculty Chairperson / Lead:");
            }
            case FACULTY_MEMBER -> {
                attr1Label.setText("Department Affiliation:");
                attr2Label.setText("Academic Title (e.g. Professor):");
            }
            case STUDENT_CLUB -> {
                attr1Label.setText("Club Category (e.g. Robotics, Arts):");
                attr2Label.setText("Faculty Advisor Name:");
            }
            case EVENT_ORGANIZER -> {
                attr1Label.setText("Event Classification:");
                attr2Label.setText("Estimated Attendance (number):");
            }
        }
        revalidate();
        repaint();
    }

    private void onAddRequester() {
        try {
            RequesterType type = (RequesterType) typeCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            PriorityTier tier = (PriorityTier) tierCombo.getSelectedItem();

            Map<String, Object> attrs = new HashMap<>();
            if (type == RequesterType.DEPARTMENT) {
                attrs.put("departmentCode", attr1Field.getText().trim());
                attrs.put("facultyLead", attr2Field.getText().trim());
            } else if (type == RequesterType.FACULTY_MEMBER) {
                attrs.put("departmentName", attr1Field.getText().trim());
                attrs.put("academicTitle", attr2Field.getText().trim());
            } else if (type == RequesterType.STUDENT_CLUB) {
                attrs.put("clubCategory", attr1Field.getText().trim());
                attrs.put("facultyAdvisor", attr2Field.getText().trim());
            } else if (type == RequesterType.EVENT_ORGANIZER) {
                attrs.put("eventType", attr1Field.getText().trim());
                attrs.put("expectedAttendance", attr2Field.getText().trim());
            }

            controller.addRequester(type, name, email, tier, attrs);

            nameField.setText("");
            emailField.setText("");
            attr1Field.setText("");
            attr2Field.setText("");

            JOptionPane.showMessageDialog(this, "Requester enrolled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onToggleActive() {
        int row = requestersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a requester from the table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            controller.toggleActiveStatus(id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemoveRequester() {
        int row = requestersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a requester from the table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Remove requester " + id + "?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.removeRequester(id);
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Requester> list = controller.getAllRequesters();
        for (Requester req : list) {
            tableModel.addRow(new Object[]{
                    req.getId(),
                    req.getName(),
                    req.getRequesterType().getDisplayName(),
                    req.getContactEmail(),
                    req.getPriorityTier(),
                    req.isActive() ? "Active" : "Inactive",
                    req.getDescription()
            });
        }
    }
}
