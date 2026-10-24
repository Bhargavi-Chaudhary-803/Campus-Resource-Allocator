package com.campus.gui;

import com.campus.service.CampusDatabase;
import com.campus.service.SimulationEngine;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * GUI COMPONENT: MAIN APPLICATION WINDOW & NAVIGATION CONTROLLER
 * =========================================================================
 * Provides the modern desktop frame with:
 * - Left Navigation Sidebar (Navy & Off-White theme)
 * - CardLayout for switching panels smoothly
 * - Real-time synchronization across panels
 * - Quick reset button to restore standard university seed data for demo
 */
public class MainFrame extends JFrame {

    private final CampusDatabase db;
    private final SimulationEngine engine;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);

    private DashboardPanel dashboardPanel;
    private ResourcePanel resourcePanel;
    private RequesterPanel requesterPanel;
    private RequestPanel requestPanel;
    private SimulationPanel simulationPanel;
    private ReportPanel reportPanel;

    private final List<JButton> navButtons = new ArrayList<>();
    private JLabel statusLabel;

    public MainFrame() {
        super("Campus Resource Allocation Simulator");
        this.db = CampusDatabase.getInstance();
        this.engine = new SimulationEngine();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.OFF_WHITE_BG);

        // 1. Left Sidebar
        JPanel sidebar = createSidebar();
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 2. Center Content Cards
        dashboardPanel = new DashboardPanel(db, engine);
        resourcePanel = new ResourcePanel(db);
        requesterPanel = new RequesterPanel(db);
        requestPanel = new RequestPanel(db);
        simulationPanel = new SimulationPanel(db, engine, this::refreshAllPanels);
        reportPanel = new ReportPanel(db, engine);

        contentCards.add(dashboardPanel, "DASHBOARD");
        contentCards.add(resourcePanel, "RESOURCES");
        contentCards.add(requesterPanel, "REQUESTERS");
        contentCards.add(requestPanel, "REQUESTS");
        contentCards.add(simulationPanel, "SIMULATION");
        contentCards.add(reportPanel, "REPORTS");

        rootPanel.add(contentCards, BorderLayout.CENTER);

        // 3. Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(6, 16, 6, 16)
        ));

        statusLabel = new JLabel("System Ready | Active Strategy: " + engine.getCurrentStrategy().getName() + " | In-Memory Storage");
        statusLabel.setFont(UITheme.regular(11.0f));
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);

        JLabel versionLabel = new JLabel("Campus Allocation Simulator v1.0 • Pure Java & Swing");
        versionLabel.setFont(UITheme.regular(11.0f));
        versionLabel.setForeground(UITheme.TEXT_MUTED);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        rootPanel.add(statusBar, BorderLayout.SOUTH);
        setContentPane(rootPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, UITheme.BORDER_COLOR));

        // Brand Banner
        JPanel brandPanel = new JPanel(new BorderLayout(0, 4));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel brandTitle = new JLabel("Campus Allocator");
        brandTitle.setFont(UITheme.bold(18.0f));
        brandTitle.setForeground(UITheme.DEEP_NAVY);

        JLabel brandSubtitle = new JLabel("Resource Simulator");
        brandSubtitle.setFont(UITheme.regular(11.0f));
        brandSubtitle.setForeground(UITheme.TEXT_SECONDARY);

        brandPanel.add(brandTitle, BorderLayout.NORTH);
        brandPanel.add(brandSubtitle, BorderLayout.SOUTH);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Menu Buttons
        JPanel navMenu = new JPanel(new GridLayout(6, 1, 0, 6));
        navMenu.setOpaque(false);
        navMenu.setBorder(new EmptyBorder(10, 12, 10, 12));

        JButton btnDash = createNavButton("📊  Dashboard", "DASHBOARD");
        JButton btnRes  = createNavButton("🏢  Campus Resources", "RESOURCES");
        JButton btnReq  = createNavButton("👥  Requesters", "REQUESTERS");
        JButton btnRqst = createNavButton("📋  Request Queue", "REQUESTS");
        JButton btnSim  = createNavButton("⚡  Run Simulation", "SIMULATION");
        JButton btnRep  = createNavButton("📈  Audit & Report", "REPORTS");

        navMenu.add(btnDash);
        navMenu.add(btnRes);
        navMenu.add(btnReq);
        navMenu.add(btnRqst);
        navMenu.add(btnSim);
        navMenu.add(btnRep);

        // Default active button
        highlightNavButton(btnDash);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(navMenu, BorderLayout.NORTH);
        sidebar.add(centerWrapper, BorderLayout.CENTER);

        // Bottom Seed Data Reset Action
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(16, 14, 20, 14));

        JButton resetSeedBtn = UITheme.createSecondaryButton("🔄 Reset to Seed Data");
        resetSeedBtn.setFont(UITheme.bold(11.0f));
        resetSeedBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Reset all resources, requesters, and requests back to initial university demo data?",
                    "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                db.seedInitialData();
                refreshAllPanels();
                JOptionPane.showMessageDialog(this, "Sample campus seed data reloaded!", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        bottomPanel.add(resetSeedBtn, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.regular(13.0f));
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addActionListener(e -> {
            cardLayout.show(contentCards, cardName);
            highlightNavButton(btn);
            refreshAllPanels();
            statusLabel.setText("Active View: " + label.trim() + " | Strategy: " + engine.getCurrentStrategy().getName());
        });

        navButtons.add(btn);
        return btn;
    }

    private void highlightNavButton(JButton activeBtn) {
        for (JButton btn : navButtons) {
            if (btn == activeBtn) {
                btn.setBackground(UITheme.SIDEBAR_ACTIVE);
                btn.setForeground(UITheme.PRIMARY_BLUE);
                btn.setFont(UITheme.bold(13.0f));
            } else {
                btn.setBackground(UITheme.SIDEBAR_BG);
                btn.setForeground(UITheme.TEXT_PRIMARY);
                btn.setFont(UITheme.regular(13.0f));
            }
        }
    }

    /**
     * Synchronizes and refreshes all child views.
     */
    public void refreshAllPanels() {
        dashboardPanel.refreshData();
        resourcePanel.refreshData();
        requesterPanel.refreshData();
        requestPanel.refreshData();
        simulationPanel.refreshResults();
        reportPanel.refreshReport();
    }
}
