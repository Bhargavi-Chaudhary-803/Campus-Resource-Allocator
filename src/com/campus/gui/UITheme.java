package com.campus.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * =========================================================================
 * GUI THEME & DESIGN SYSTEM: BLUE & OFF-WHITE WITH TIGHT-SPACED INTER FONT
 * =========================================================================
 * Provides clean, modern, student-friendly visual styling across all Swing components.
 */
public final class UITheme {

    // Simple Blue & Offwhite Color Palette
    public static final Color OFF_WHITE_BG      = new Color(248, 250, 252); // #F8FAFC
    public static final Color CARD_BG           = Color.WHITE;              // #FFFFFF
    public static final Color SIDEBAR_BG        = new Color(241, 245, 249); // #F1F5F9
    public static final Color SIDEBAR_ACTIVE    = new Color(219, 234, 254); // #DBEAFE
    public static final Color BORDER_COLOR      = new Color(226, 232, 240); // #E2E8F0

    public static final Color PRIMARY_BLUE      = new Color(37, 99, 235);   // #2563EB
    public static final Color PRIMARY_BLUE_DARK = new Color(29, 78, 216);   // #1D4ED8
    public static final Color DEEP_NAVY         = new Color(30, 58, 138);   // #1E3A8A

    public static final Color TEXT_PRIMARY      = new Color(15, 23, 42);    // #0F172A
    public static final Color TEXT_SECONDARY    = new Color(100, 116, 139); // #64748B
    public static final Color TEXT_MUTED        = new Color(148, 163, 184); // #94A3B8

    // Status Badges (Subtle Pastel Tones)
    public static final Color BADGE_GREEN_BG    = new Color(236, 253, 245);
    public static final Color BADGE_GREEN_TEXT  = new Color(6, 95, 70);
    public static final Color BADGE_AMBER_BG    = new Color(254, 243, 199);
    public static final Color BADGE_AMBER_TEXT  = new Color(146, 64, 14);
    public static final Color BADGE_RED_BG      = new Color(254, 242, 242);
    public static final Color BADGE_RED_TEXT    = new Color(153, 27, 27);
    public static final Color BADGE_BLUE_BG     = new Color(239, 246, 255);
    public static final Color BADGE_BLUE_TEXT   = new Color(30, 64, 175);

    private static Font interFont;

    static {
        loadInterFont();
    }

    private UITheme() {}

    private static void loadInterFont() {
        try {
            File fontFile = new File("resources/Inter.ttf");
            if (fontFile.exists()) {
                try (InputStream is = new FileInputStream(fontFile)) {
                    interFont = Font.createFont(Font.TRUETYPE_FONT, is);
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(interFont);
                }
            } else {
                interFont = new Font("Inter", Font.PLAIN, 12);
            }
        } catch (Exception ex) {
            // Fallback to Segoe UI or SansSerif
            interFont = new Font("Segoe UI", Font.PLAIN, 12);
        }
    }

    /**
     * Tightly spaced Inter font helper
     */
    public static Font font(int style, float size) {
        if (interFont != null) {
            return interFont.deriveFont(style, size);
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
     * Applies default theme settings to standard Swing components.
     */
    public static void applyGlobalDefaults() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        Font standard = regular(12.0f);
        Font boldStandard = bold(13.0f);

        UIManager.put("Label.font", standard);
        UIManager.put("Button.font", regular(12.0f));
        UIManager.put("TextField.font", standard);
        UIManager.put("TextArea.font", standard);
        UIManager.put("ComboBox.font", standard);
        UIManager.put("Table.font", standard);
        UIManager.put("TableHeader.font", boldStandard);
        UIManager.put("Panel.background", OFF_WHITE_BG);
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        );
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(bold(12.0f));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_BLUE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(regular(12.0f));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(CARD_BG);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, false),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        return btn;
    }

    public static void styleTable(JTable table) {
        table.setFont(regular(12.0f));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(bold(12.0f));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
    }

    public static DefaultTableCellRenderer createStatusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.bold(11.0f));

                if (value == null) return lbl;
                String text = value.toString();

                if (text.equalsIgnoreCase("Allocated") || text.equalsIgnoreCase("Available")) {
                    lbl.setBackground(BADGE_GREEN_BG);
                    lbl.setForeground(BADGE_GREEN_TEXT);
                } else if (text.equalsIgnoreCase("Waitlisted") || text.equalsIgnoreCase("Maintenance")) {
                    lbl.setBackground(BADGE_AMBER_BG);
                    lbl.setForeground(BADGE_AMBER_TEXT);
                } else if (text.equalsIgnoreCase("Rejected") || text.equalsIgnoreCase("Cancelled")) {
                    lbl.setBackground(BADGE_RED_BG);
                    lbl.setForeground(BADGE_RED_TEXT);
                } else {
                    lbl.setBackground(BADGE_BLUE_BG);
                    lbl.setForeground(BADGE_BLUE_TEXT);
                }
                lbl.setOpaque(true);
                return lbl;
            }
        };
    }
}
