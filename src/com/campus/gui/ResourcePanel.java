package com.campus.gui;

import com.campus.model.*;
import com.campus.service.CampusDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: RESOURCE MANAGEMENT PANEL
 * =========================================================================
 * Demonstrates:
 * - Resource listing with polymorphic capability extraction
 * - Maintenance toggle functionality
 * - Resource creation via ResourceFactory (Factory Pattern in Action)
 */
public class ResourcePanel extends JPanel {

    private final CampusDatabase db;
    private JTable resourceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> typeFilterCombo;
    private JTextField searchField;

    public ResourcePanel(CampusDatabase db) {
        this.db = db;
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.OFF_WHITE_BG);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- 1. Top Panel: Title and Filter Controls ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);

        // Header Title
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Campus Resource Catalog");
        title.setFont(UITheme.bold(20.0f));
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage lecture halls, research laboratories, auditoriums, and portable audiovisual equipment.");
        subtitle.setFont(UITheme.regular(12.0f));
        subtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // Filter / Search Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JLabel filterLbl = new JLabel("Filter Type:");
        filterLbl.setFont(UITheme.bold(12.0f));
        typeFilterCombo = new JComboBox<>(new String[]{"All Resources", "Classrooms", "Laboratories", "Auditoriums", "Equipment"});
        typeFilterCombo.setBackground(Color.WHITE);
        typeFilterCombo.addActionListener(e -> applyFilter());

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UITheme.bold(12.0f));
        searchField = new JTextField(15);
        searchField.addActionListener(e -> applyFilter());

        JButton applyBtn = UITheme.createSecondaryButton("Search");
        applyBtn.addActionListener(e -> applyFilter());

        JButton toggleMaintBtn = UITheme.createSecondaryButton("Toggle Maintenance");
        toggleMaintBtn.addActionListener(e -> handleToggleMaintenance());

        JButton addResourceBtn = UITheme.createPrimaryButton("+ Add New Resource");
        addResourceBtn.addActionListener(e -> showAddResourceDialog());

        toolbar.add(filterLbl);
        toolbar.add(typeFilterCombo);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(searchLbl);
        toolbar.add(searchField);
        toolbar.add(applyBtn);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(toggleMaintBtn);
        toolbar.add(addResourceBtn);

        topPanel.add(toolbar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center: Table Card ---
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.createCardBorder());

        String[] columns = {"ID", "Resource Name", "Type", "Location", "Capacity", "Hourly Rate", "Status", "Specialized Features"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resourceTable = new JTable(tableModel);
        UITheme.styleTable(resourceTable);
        resourceTable.getColumnModel().getColumn(6).setCellRenderer(UITheme.createStatusRenderer());

        sorter = new TableRowSorter<>(tableModel);
        resourceTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(resourceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    private void applyFilter() {
        String selectedType = (String) typeFilterCombo.getSelectedItem();
        String searchText = searchField.getText().trim();

        RowFilter<DefaultTableModel, Object> typeFilter = null;
        if (selectedType != null && !selectedType.equals("All Resources")) {
            String typeMatch = selectedType.substring(0, selectedType.length() - 1); // remove trailing 's'
            typeFilter = RowFilter.regexFilter("(?i)" + typeMatch, 2);
        }

        RowFilter<DefaultTableModel, Object> textFilter = null;
        if (!searchText.isEmpty()) {
            textFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 3);
        }

        if (typeFilter != null && textFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(typeFilter, textFilter)));
        } else if (typeFilter != null) {
            sorter.setRowFilter(typeFilter);
        } else if (textFilter != null) {
            sorter.setRowFilter(textFilter);
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void handleToggleMaintenance() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a resource from the table to toggle its maintenance status.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = resourceTable.convertRowIndexToModel(selectedRow);
        String resId = (String) tableModel.getValueAt(modelRow, 0);

        db.findResourceById(resId).ifPresent(res -> {
            if (res.isUnderMaintenance()) {
                res.setUnderMaintenance(false);
                JOptionPane.showMessageDialog(this,
                        res.getName() + " is now marked AVAILABLE.",
                        "Maintenance Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                res.setUnderMaintenance(true);
                JOptionPane.showMessageDialog(this,
                        res.getName() + " is now placed under SCHEDULED MAINTENANCE.\n" +
                                "The engine will automatically block new bookings for this resource.",
                        "Maintenance Mode", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshData();
        });
    }

    private void showAddResourceDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Campus Resource", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 14));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JComboBox<ResourceType> typeCombo = new JComboBox<>(ResourceType.values());
        JTextField idField = new JTextField("RES-" + (db.getResources().size() + 1));
        JTextField nameField = new JTextField();
        JTextField locField = new JTextField();
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(50, 1, 1000, 5));
        JTextField extraField = new JTextField("Standard equipment & AV");

        formPanel.add(new JLabel("Resource Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Resource ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name / Label:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Campus Location:"));
        formPanel.add(locField);
        formPanel.add(new JLabel("Seat / Unit Capacity:"));
        formPanel.add(capacitySpinner);
        formPanel.add(new JLabel("Room / Type Feature:"));
        formPanel.add(extraField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(UITheme.OFF_WHITE_BG);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UITheme.createPrimaryButton("Create Resource");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String loc = locField.getText().trim();
            int cap = (int) capacitySpinner.getValue();
            String extra = extraField.getText().trim();

            if (name.isEmpty() || loc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all resource fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // [DESIGN PATTERN - FACTORY METHOD]: Creating concrete subclasses polymorphically
            Resource newRes = ResourceFactory.createResource(
                    (ResourceType) typeCombo.getSelectedItem(),
                    idField.getText().trim(),
                    name,
                    loc,
                    cap,
                    extra
            );

            db.addResource(newRes);
            refreshData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Resource created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Resource> list = db.getResources();

        for (Resource r : list) {
            String status = r.isUnderMaintenance() ? "Maintenance" :
                    (r.isBooked() ? "Allocated" : "Available");

            // [OOP - POLYMORPHISM]: Calling calculateOperatingCostPerHour() and getDetails() on abstract Resource reference
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getName(),
                    r.getType().getDisplayName(),
                    r.getLocation(),
                    r.getCapacity(),
                    String.format("$%.2f/hr", r.calculateOperatingCostPerHour()),
                    status,
                    r.getDetails()
            });
        }
    }
}
