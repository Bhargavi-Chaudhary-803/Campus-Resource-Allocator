package com.campus.simulator.ui.component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Aesthetic configuration providing a clean, subtle, neutral design system
 * using tightly spaced Inter typography across all Swing components.
 */
public final class UITheme {

    private static final Logger LOGGER = Logger.getLogger(UITheme.class.getName());

    // Neutral Color Palette
    public static final Color COLOR_APP_BG = new Color(248, 250, 252);       // #F8FAFC
    public static final Color COLOR_CARD_BG = Color.WHITE;                    // #FFFFFF
    public static final Color COLOR_SIDEBAR_BG = new Color(241, 245, 249);   // #F1F5F9
    public static final Color COLOR_SIDEBAR_HOVER = new Color(226, 232, 240);// #E2E8F0
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(219, 234, 254);// #DBEAFE
    public static final Color COLOR_BORDER = new Color(226, 232, 240);       // #E2E8F0
    public static final Color COLOR_BORDER_FOCUSED = new Color(59, 130, 246);// #3B82F6

    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);    // #0F172A
    public static final Color COLOR_TEXT_SECONDARY = new Color(100, 116, 139);// #64748B
    public static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);   // #94A3B8

    public static final Color COLOR_PRIMARY = new Color(30, 41, 59);         // #1E293B
    public static final Color COLOR_PRIMARY_HOVER = new Color(15, 23, 42);   // #0F172A
    public static final Color COLOR_ACCENT_BLUE = new Color(37, 99, 235);    // #2563EB

    // Subtle Badges
    public static final Color COLOR_SUCCESS_BG = new Color(236, 253, 245);
    public static final Color COLOR_SUCCESS_TEXT = new Color(6, 95, 70);
    public static final Color COLOR_WARNING_BG = new Color(254, 243, 199);
    public static final Color COLOR_WARNING_TEXT = new Color(146, 64, 14);
    public static final Color COLOR_DANGER_BG = new Color(254, 242, 242);
    public static final Color COLOR_DANGER_TEXT = new Color(153, 27, 27);
    public static final Color COLOR_INFO_BG = new Color(239, 246, 255);
    public static final Color COLOR_INFO_TEXT = new Color(30, 64, 175);

    private static Font baseInterFont;

    static {
        loadInterFont();
    }

    private UITheme() {
        // Prevent instantiation
    }

    private static void loadInterFont() {
        try (InputStream is = UITheme.class.getResourceAsStream("/fonts/Inter.ttf")) {
            if (is != null) {
                baseInterFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseInterFont);
                LOGGER.info("Successfully loaded and registered Inter font from classpath resources");
            } else {
                baseInterFont = new Font("Inter", Font.PLAIN, 12);
                LOGGER.info("Inter.ttf resource stream was null; falling back to system Inter");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to load bundled Inter font; using system fallback", ex);
            baseInterFont = new Font("Segoe UI", Font.PLAIN, 12);
        }
    }

    public static Font font(int style, float size) {
        if (baseInterFont != null) {
            return baseInterFont.deriveFont(style, size);
        }
        return new Font("Segoe UI", style, (int) size);
    }

    public static Font regular(float size) {
        return font(Font.PLAIN, size);
    }

    public static Font bold(float size) {
        return font(Font.BOLD, size);
    }

    /**
     * Initializes global Swing Look and Feel and typography defaults.
     */
    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep default L&F if system cannot be initialized
        }

        Font defaultFont = regular(12.5f);
        Font titleFont = bold(13.5f);

        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", regular(12.0f));
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Table.font", regular(12.0f));
        UIManager.put("TableHeader.font", titleFont);
        UIManager.put("TitledBorder.font", titleFont);
        UIManager.put("Panel.background", COLOR_APP_BG);
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        );
    }

    public static Border createFieldBorder() {
        return new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, false),
                new EmptyBorder(6, 8, 6, 8)
        );
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(regular(12.0f));
        button.setForeground(Color.WHITE);
        button.setBackground(COLOR_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(7, 14, 7, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(regular(12.0f));
        button.setForeground(COLOR_TEXT_PRIMARY);
        button.setBackground(COLOR_CARD_BG);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, false),
                new EmptyBorder(6, 12, 6, 12)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    public static void styleTable(JTable table) {
        table.setFont(regular(12.0f));
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(COLOR_BORDER);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(COLOR_TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(bold(12.0f));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 34));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
