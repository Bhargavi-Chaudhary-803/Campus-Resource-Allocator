package com.campus.simulator.ui.component;

import com.campus.simulator.model.allocation.AllocationOutcome;
import com.campus.simulator.model.allocation.AllocationStatus;
import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.resource.ResourceStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer displaying clean, rounded status badges.
 */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    private final JLabel badgeLabel = new JLabel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 8, 12, 12);
            super.paintComponent(g);
            g2.dispose();
        }
    };

    public StatusBadgeRenderer() {
        badgeLabel.setOpaque(false);
        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        badgeLabel.setFont(UITheme.bold(11.0f));
        badgeLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        if (value == null) {
            badgeLabel.setText("-");
            badgeLabel.setBackground(Color.WHITE);
            badgeLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
            return badgeLabel;
        }

        String text = value.toString();
        badgeLabel.setText(text);

        if (value instanceof ResourceStatus status) {
            switch (status) {
                case AVAILABLE -> setColors(UITheme.COLOR_SUCCESS_BG, UITheme.COLOR_SUCCESS_TEXT);
                case MAINTENANCE -> setColors(UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING_TEXT);
                case RETIRED -> setColors(UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER_TEXT);
            }
        } else if (value instanceof RequestStatus status) {
            switch (status) {
                case APPROVED, COMPLETED -> setColors(UITheme.COLOR_SUCCESS_BG, UITheme.COLOR_SUCCESS_TEXT);
                case PENDING -> setColors(UITheme.COLOR_INFO_BG, UITheme.COLOR_INFO_TEXT);
                case WAITLISTED -> setColors(UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING_TEXT);
                case REJECTED, CANCELLED -> setColors(UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER_TEXT);
            }
        } else if (value instanceof AllocationStatus status) {
            switch (status) {
                case ACTIVE -> setColors(UITheme.COLOR_SUCCESS_BG, UITheme.COLOR_SUCCESS_TEXT);
                case RELEASED -> setColors(UITheme.COLOR_INFO_BG, UITheme.COLOR_INFO_TEXT);
                case COMPLETED -> setColors(new Color(241, 245, 249), UITheme.COLOR_TEXT_PRIMARY);
                case CANCELLED -> setColors(UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER_TEXT);
            }
        } else if (value instanceof AllocationOutcome outcome) {
            switch (outcome) {
                case APPROVED -> setColors(UITheme.COLOR_SUCCESS_BG, UITheme.COLOR_SUCCESS_TEXT);
                case WAITLISTED -> setColors(UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING_TEXT);
                case REJECTED -> setColors(UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER_TEXT);
            }
        } else if (value instanceof PriorityTier tier) {
            switch (tier) {
                case CRITICAL -> setColors(UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER_TEXT);
                case HIGH -> setColors(UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING_TEXT);
                case MEDIUM -> setColors(UITheme.COLOR_INFO_BG, UITheme.COLOR_INFO_TEXT);
                case LOW -> setColors(new Color(241, 245, 249), UITheme.COLOR_TEXT_SECONDARY);
            }
        } else if (value instanceof RequestPriority priority) {
            switch (priority) {
                case URGENT -> setColors(UITheme.COLOR_DANGER_BG, UITheme.COLOR_DANGER_TEXT);
                case HIGH -> setColors(UITheme.COLOR_WARNING_BG, UITheme.COLOR_WARNING_TEXT);
                case NORMAL -> setColors(UITheme.COLOR_INFO_BG, UITheme.COLOR_INFO_TEXT);
                case LOW -> setColors(new Color(241, 245, 249), UITheme.COLOR_TEXT_SECONDARY);
            }
        } else {
            setColors(new Color(241, 245, 249), UITheme.COLOR_TEXT_PRIMARY);
        }

        return badgeLabel;
    }

    private void setColors(Color bg, Color fg) {
        badgeLabel.setBackground(bg);
        badgeLabel.setForeground(fg);
    }
}
