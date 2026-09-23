package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomTabUI extends BasicTabbedPaneUI {
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected) {
            g2.setColor(CateringManagementSystem.ACCENT_BLUE);
        } else {
            g2.setColor(CateringManagementSystem.CARD_BG);
        }
        g2.fillRect(x, y, w, h);
        g2.setColor(CateringManagementSystem.ACCENT_BLUE_BORDER);
        g2.drawRect(x, y, w, h);
        g2.dispose();
    }
}
