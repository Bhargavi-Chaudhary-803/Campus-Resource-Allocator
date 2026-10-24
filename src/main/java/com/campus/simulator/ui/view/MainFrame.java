package com.campus.simulator.ui.view;

import com.campus.simulator.SimulationContext;
import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.controller.DashboardController;
import com.campus.simulator.ui.controller.ReportController;
import com.campus.simulator.ui.controller.RequesterController;
import com.campus.simulator.ui.controller.RequestController;
import com.campus.simulator.ui.controller.ResourceController;
import com.campus.simulator.ui.controller.SimulationController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application window for the Campus Resource Allocation Simulator.
 * Features clean left navigation switching between primary views, subtle Inter typography,
 * and a status bar.
 */
public class MainFrame extends JFrame {

    private final SimulationContext context;

    private CardLayout cardLayout;
    private JPanel contentContainer;
    private JLabel statusBarLabel;

    private final List<JButton> navButtons = new ArrayList<>();

    public MainFrame(SimulationContext context) {
        this.context = context;

        setTitle("Campus Resource Allocation Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.COLOR_APP_BG);

        // 1. Left Navigation Sidebar
        JPanel sidebar = createSidebar();
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. CardLayout Content Area
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(UITheme.COLOR_APP_BG);

        DashboardPanel dashboardPanel = new DashboardPanel(new DashboardController(context));
        ResourcesPanel resourcesPanel = new ResourcesPanel(new ResourceController(context));
        RequestersPanel requestersPanel = new RequestersPanel(new RequesterController(context));
        RequestsPanel requestsPanel = new RequestsPanel(new RequestController(context));
        SimulationPanel simulationPanel = new SimulationPanel(new SimulationController(context));
        ReportsPanel reportsPanel = new ReportsPanel(new ReportController(context));

        contentContainer.add(dashboardPanel, "Dashboard");
        contentContainer.add(resourcesPanel, "Resources");
        contentContainer.add(requestersPanel, "Requesters");
        contentContainer.add(requestsPanel, "Requests");
        contentContainer.add(simulationPanel, "Allocation Simulation");
        contentContainer.add(reportsPanel, "Reports");

        rootPanel.add(contentContainer, BorderLayout.CENTER);

        // 3. Status Bar at Bottom
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UITheme.COLOR_CARD_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, UITheme.COLOR_BORDER),
                new EmptyBorder(6, 16, 6, 16)
        ));

        statusBarLabel = new JLabel("System operational. Seed dataset loaded in memory. Active strategy: Priority-First.");
        statusBarLabel.setFont(UITheme.regular(11.5f));
        statusBarLabel.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        JLabel versionLabel = new JLabel("v1.0.0 | Pure Java 17+ & Swing");
        versionLabel.setFont(UITheme.regular(11.0f));
        versionLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        statusBar.add(statusBarLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);
        rootPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);

        // Default to Dashboard tab
        selectNavigationTab("Dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(UITheme.COLOR_SIDEBAR_BG);
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, UITheme.COLOR_BORDER));

        // Brand / Title Header
        JPanel brandPanel = new JPanel(new BorderLayout(4, 4));
        brandPanel.setBackground(UITheme.COLOR_SIDEBAR_BG);
        brandPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel brandTitle = new JLabel("CAMPUS ALLOCATOR");
        brandTitle.setFont(UITheme.bold(14.0f));
        brandTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel brandSub = new JLabel("Resource Simulator");
        brandSub.setFont(UITheme.regular(12.0f));
        brandSub.setForeground(UITheme.COLOR_TEXT_SECONDARY);

        brandPanel.add(brandTitle, BorderLayout.NORTH);
        brandPanel.add(brandSub, BorderLayout.SOUTH);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Items
        JPanel navList = new JPanel();
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        navList.setBackground(UITheme.COLOR_SIDEBAR_BG);
        navList.setBorder(new EmptyBorder(8, 12, 12, 12));

        String[] sections = {
                "Dashboard",
                "Resources",
                "Requesters",
                "Requests",
                "Allocation Simulation",
                "Reports"
        };

        for (String section : sections) {
            JButton btn = createNavButton(section);
            navButtons.add(btn);
            navList.add(btn);
            navList.add(Box.createVerticalStrut(4));
        }

        sidebar.add(navList, BorderLayout.CENTER);

        // Bottom Architecture Badge
        JPanel bottomBadge = new JPanel(new BorderLayout());
        bottomBadge.setBackground(UITheme.COLOR_SIDEBAR_BG);
        bottomBadge.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel archLbl = new JLabel("Clean Architecture");
        archLbl.setFont(UITheme.bold(11.0f));
        archLbl.setForeground(UITheme.COLOR_TEXT_MUTED);

        JLabel solidLbl = new JLabel("Strict OOP & SOLID Domain");
        solidLbl.setFont(UITheme.regular(10.5f));
        solidLbl.setForeground(UITheme.COLOR_TEXT_MUTED);

        bottomBadge.add(archLbl, BorderLayout.NORTH);
        bottomBadge.add(solidLbl, BorderLayout.SOUTH);
        sidebar.add(bottomBadge, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String name) {
        JButton btn = new JButton(name);
        btn.setFont(UITheme.regular(12.5f));
        btn.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        btn.setBackground(UITheme.COLOR_SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setOpaque(true);

        btn.addActionListener(e -> selectNavigationTab(name));
        return btn;
    }

    private void selectNavigationTab(String name) {
        cardLayout.show(contentContainer, name);

        for (JButton b : navButtons) {
            if (b.getText().equalsIgnoreCase(name)) {
                b.setBackground(UITheme.COLOR_SIDEBAR_ACTIVE);
                b.setFont(UITheme.bold(12.5f));
                b.setForeground(UITheme.COLOR_ACCENT_BLUE);
            } else {
                b.setBackground(UITheme.COLOR_SIDEBAR_BG);
                b.setFont(UITheme.regular(12.5f));
                b.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            }
        }

        statusBarLabel.setText("Viewing screen: " + name + " | Simulator operational");
    }
}
