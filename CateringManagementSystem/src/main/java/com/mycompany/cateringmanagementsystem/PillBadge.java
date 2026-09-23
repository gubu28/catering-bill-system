package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class PillBadge extends JLabel {
    private static final long serialVersionUID = 1L;
    private Color badgeBg;

    public PillBadge(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 10));
        setForeground(Color.WHITE);
        setBorder(new EmptyBorder(3, 8, 3, 8));

        switch (text.toLowerCase()) {
            case "veg":
            case "completed":
                badgeBg = CateringManagementSystem.ACCENT_GREEN;
                break;
            case "non veg":
            case "cancelled":
                badgeBg = CateringManagementSystem.ACCENT_RED;
                break;
            case "dessert":
            case "pending":
                badgeBg = CateringManagementSystem.ACCENT_AMBER;
                break;
            case "confirmed":
            default:
                badgeBg = CateringManagementSystem.ACCENT_BLUE;
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(badgeBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
