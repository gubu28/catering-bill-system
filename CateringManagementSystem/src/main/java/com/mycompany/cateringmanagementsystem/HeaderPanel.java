package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HeaderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private DataManager dataManager;
    private JLabel revLabel, ordersLabel, guestsLabel, menuLabel;

    public HeaderPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout());
        setBackground(CateringManagementSystem.BG_DARK);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel brandPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        brandPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🍽️ CATERING MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(CateringManagementSystem.TEXT_WHITE);

        JLabel subtitleLabel = new JLabel("Executive Enterprise Platform - Live PDF Bill & Payment Sync Engine");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(CateringManagementSystem.TEXT_MUTED);

        brandPanel.add(titleLabel);
        brandPanel.add(subtitleLabel);

        JPanel metricsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        metricsContainer.setOpaque(false);

        revLabel = new JLabel();
        ordersLabel = new JLabel();
        guestsLabel = new JLabel();
        menuLabel = new JLabel();

        metricsContainer.add(createMetricCard("Total Revenue", revLabel, CateringManagementSystem.ACCENT_GREEN));
        metricsContainer.add(createMetricCard("Active Bookings", ordersLabel, CateringManagementSystem.ACCENT_BLUE));
        metricsContainer.add(createMetricCard("Guests Served", guestsLabel, CateringManagementSystem.ACCENT_AMBER));
        metricsContainer.add(createMetricCard("Catalog Items", menuLabel, CateringManagementSystem.ACCENT_PURPLE));

        add(brandPanel, BorderLayout.WEST);
        add(metricsContainer, BorderLayout.EAST);
        updateMetrics();
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CateringManagementSystem.PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accentColor);
                g2.fillRect(0, 0, 4, getHeight());
                g2.dispose();
            }
        };
        card.setLayout(new GridLayout(2, 1, 0, 2));
        card.setPreferredSize(new Dimension(150, 48));
        card.setBorder(new EmptyBorder(6, 12, 6, 8));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
        titleLbl.setForeground(CateringManagementSystem.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(CateringManagementSystem.TEXT_WHITE);

        card.add(titleLbl);
        card.add(valueLabel);
        return card;
    }

    public void updateMetrics() {
        double totalRevenue = dataManager.getOrders().stream()
                .filter(o -> !o.getStatus().equalsIgnoreCase("Cancelled"))
                .mapToDouble(Order::getTotalPrice)
                .sum();
        long activeOrders = dataManager.getOrders().stream()
                .filter(o -> o.getStatus().equalsIgnoreCase("Pending") || o.getStatus().equalsIgnoreCase("Confirmed"))
                .count();
        int totalGuests = dataManager.getOrders().stream()
                .filter(o -> !o.getStatus().equalsIgnoreCase("Cancelled"))
                .mapToInt(Order::getGuestCount)
                .sum();
        int menuCount = dataManager.getMenuItems().size();

        revLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(totalRevenue));
        ordersLabel.setText(String.valueOf(activeOrders));
        guestsLabel.setText(String.format("%,d", totalGuests));
        menuLabel.setText(String.valueOf(menuCount));
    }
}
