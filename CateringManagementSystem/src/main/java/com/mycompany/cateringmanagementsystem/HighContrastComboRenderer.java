package com.mycompany.cateringmanagementsystem;

import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

public class HighContrastComboRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (isSelected) {
            l.setBackground(CateringManagementSystem.ACCENT_BLUE);
            l.setForeground(CateringManagementSystem.TEXT_WHITE);
        } else {
            l.setBackground(CateringManagementSystem.PANEL_BG);
            l.setForeground(CateringManagementSystem.TEXT_WHITE);
        }
        l.setBorder(new EmptyBorder(4, 8, 4, 8));
        return l;
    }
}
