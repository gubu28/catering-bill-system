package com.mycompany.cateringmanagementsystem;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        String status = (value != null) ? value.toString() : "Pending";
        PillBadge badge = new PillBadge(status);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        return badge;
    }
}
