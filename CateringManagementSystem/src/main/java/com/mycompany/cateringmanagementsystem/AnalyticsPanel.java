package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AnalyticsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private DataManager dataManager;

    private JLabel avgOrderLabel, vegRatioLabel, balanceTotalLabel, totalDishesLabel;

    public AnalyticsPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new GridLayout(2, 2, 15, 15));
        setBackground(CateringManagementSystem.BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        avgOrderLabel = new JLabel();
        vegRatioLabel = new JLabel();
        balanceTotalLabel = new JLabel();
        totalDishesLabel = new JLabel();

        add(createAnalyticsCard("Average Booking Value", avgOrderLabel, "Calculated across active catering orders", CateringManagementSystem.ACCENT_BLUE));
        add(createAnalyticsCard("Dietary Preference Ratio", vegRatioLabel, "Veg vs Non-Veg Order Distribution", CateringManagementSystem.ACCENT_GREEN));
        add(createAnalyticsCard("Pending Balance Collection", balanceTotalLabel, "Total uncollected customer balances", CateringManagementSystem.ACCENT_AMBER));
        add(createAnalyticsCard("Catalog Capacity", totalDishesLabel, "Total active dishes available in system", CateringManagementSystem.ACCENT_PURPLE));

        updateAnalytics();
    }

    private JPanel createAnalyticsCard(String title, JLabel valueLbl, String desc, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CateringManagementSystem.PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(accent);
                g2.fillRect(0, 0, 6, getHeight());
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tLbl.setForeground(CateringManagementSystem.TEXT_MUTED);

        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLbl.setForeground(CateringManagementSystem.TEXT_WHITE);

        JLabel dLbl = new JLabel(desc);
        dLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dLbl.setForeground(CateringManagementSystem.TEXT_MUTED);

        card.add(tLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        card.add(dLbl, BorderLayout.SOUTH);

        return card;
    }

    public void updateAnalytics() {
        List<Order> validOrders = dataManager.getOrders().stream()
                .filter(o -> !o.getStatus().equalsIgnoreCase("Cancelled"))
                .collect(Collectors.toList());

        double avgVal = validOrders.isEmpty() ? 0 : validOrders.stream().mapToDouble(Order::getTotalPrice).average().orElse(0);
        double totalBalance = validOrders.stream().mapToDouble(Order::getBalanceDue).sum();

        long vegCount = validOrders.stream().filter(o -> o.getMenuType().equalsIgnoreCase("Veg")).count();
        long nonVegCount = validOrders.stream().filter(o -> o.getMenuType().equalsIgnoreCase("Non Veg")).count();
        long totalCount = validOrders.size();

        String ratio = totalCount == 0 ? "N/A" : String.format("%d%% Veg / %d%% Non-Veg",
                (vegCount * 100) / Math.max(1, totalCount),
                (nonVegCount * 100) / Math.max(1, totalCount));

        avgOrderLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(avgVal));
        vegRatioLabel.setText(ratio);
        balanceTotalLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(totalBalance));
        totalDishesLabel.setText(dataManager.getMenuItems().size() + " Dishes Available");
    }
}
