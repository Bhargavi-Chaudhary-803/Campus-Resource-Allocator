package com.campus.gui;

import com.campus.model.*;
import com.campus.service.CampusDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: REQUESTER DIRECTORY PANEL
 * =========================================================================
 * Demonstrates:
 * - Polymorphic display of campus actors (Department, Faculty, StudentClub)
 * - Base priority tier evaluation
 * - Dynamic allocation load tracking for the Fairness Strategy
 */
public class RequesterPanel extends JPanel {

    private final CampusDatabase db;
    private JTable requesterTable;
    private DefaultTableModel tableModel;

    public RequesterPanel(CampusDatabase db) {
        this.db = db;
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.OFF_WHITE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- 1. Top Panel: Title and Add Action ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Campus Requester Directory");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Academic departments, teaching faculty, and recognized student organizations requesting campus infrastructure.");
        subtitle.setFont(UITheme.regular(12.0f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        actionPanel.setOpaque(false);
        JButton addRequesterBtn = UITheme.createPrimaryButton("+ Register Requester");
        addRequesterBtn.addActionListener(e -> showAddRequesterDialog());
        actionPanel.add(addRequesterBtn);

        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center: Table Card ---
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.createCardBorder());

        String[] columns = {"ID", "Entity / Person Name", "Type", "Contact Email", "Priority Tier", "Base Weight", "Active Allocations", "Organizational Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requesterTable = new JTable(tableModel);
        UITheme.styleTable(requesterTable);
        requesterTable.getColumnModel().getColumn(4).setCellRenderer(UITheme.createStatusRenderer());

        JScrollPane scrollPane = new JScrollPane(requesterTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void showAddRequesterDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register Campus Requester", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 14));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JComboBox<RequesterType> typeCombo = new JComboBox<>(RequesterType.values());
        JTextField idField = new JTextField("REQ-0" + (db.getRequesters().size() + 1));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField("@campus.edu");
        JComboBox<PriorityLevel> priorityCombo = new JComboBox<>(PriorityLevel.values());
        JTextField detailField = new JTextField();

        formPanel.add(new JLabel("Requester Category:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Requester ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Full Name / Entity:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email Address:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Priority Tier:"));
        formPanel.add(priorityCombo);
        formPanel.add(new JLabel("Dept / Advisor Info:"));
        formPanel.add(detailField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(UITheme.OFF_WHITE_BG);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Register");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String detail = detailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all requester fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RequesterType type = (RequesterType) typeCombo.getSelectedItem();
            PriorityLevel priority = (PriorityLevel) priorityCombo.getSelectedItem();
            String id = idField.getText().trim();

            Requester newRequester;
            // [OOP - INHERITANCE & POLYMORPHISM]: Instantiating specific subclass
            if (type == RequesterType.DEPARTMENT) {
                newRequester = new Department(id, name, email, priority, "ACAD", detail.isEmpty() ? "Dean's Office" : detail);
            } else if (type == RequesterType.FACULTY) {
                newRequester = new FacultyMember(id, name, email, priority, "Faculty Member", detail.isEmpty() ? "Academics" : detail);
            } else {
                newRequester = new StudentClub(id, name, email, priority, "Student Activities", detail.isEmpty() ? "Faculty Advisor" : detail);
            }

            db.addRequester(newRequester);
            refreshData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Requester registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Requester> list = db.getRequesters();

        for (Requester r : list) {
            String specificDetails = "";
            if (r instanceof Department d) {
                specificDetails = "Dept Code: " + d.getDepartmentCode() + " | Head: " + d.getHeadOfDepartment();
            } else if (r instanceof FacultyMember f) {
                specificDetails = "Rank: " + f.getAcademicRank() + " | Dept: " + f.getDepartmentAffiliation();
            } else if (r instanceof StudentClub c) {
                specificDetails = "Category: " + c.getClubCategory() + " | Advisor: " + c.getFacultyAdvisor();
            }

            // [OOP - POLYMORPHISM]: getBasePriorityScore() called dynamically on subclass
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getName(),
                    r.getType().getDisplayName(),
                    r.getEmail(),
                    r.getPriority().name(),
                    r.getBasePriorityScore() + " pts",
                    r.getAllocationCount(),
                    specificDetails
            });
        }
    }
}
