package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class DangerButton extends JButton {
    private static final long serialVersionUID = 1L;

    public DangerButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setBackground(CateringManagementSystem.ACCENT_RED);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? CateringManagementSystem.ACCENT_RED.darker() : CateringManagementSystem.ACCENT_RED);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(new Color(248, 113, 113));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
