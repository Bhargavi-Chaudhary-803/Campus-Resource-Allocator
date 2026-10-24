package com.campus.simulator.ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Clean card container component with a subtle border and neutral background.
 */
public class CardPanel extends JPanel {

    private final String title;

    public CardPanel() {
        this(null);
    }

    public CardPanel(String title) {
        this.title = title;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.COLOR_CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        if (title != null && !title.isBlank()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(UITheme.bold(14.0f));
            titleLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
            add(titleLabel, BorderLayout.NORTH);
        }
    }
}
