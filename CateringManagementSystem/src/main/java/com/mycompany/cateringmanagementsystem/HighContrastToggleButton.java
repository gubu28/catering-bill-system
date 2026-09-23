package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

public class HighContrastToggleButton extends JToggleButton {
    private static final long serialVersionUID = 1L;

    public HighContrastToggleButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(6, 12, 6, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected()) {
            g2.setColor(CateringManagementSystem.ACCENT_BLUE);
            setForeground(CateringManagementSystem.TEXT_WHITE);
        } else {
            g2.setColor(CateringManagementSystem.CARD_BG);
            setForeground(CateringManagementSystem.TEXT_WHITE);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(CateringManagementSystem.ACCENT_BLUE_BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
