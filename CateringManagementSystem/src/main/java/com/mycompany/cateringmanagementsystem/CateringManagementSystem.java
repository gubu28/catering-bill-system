package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;

/**
 * Advanced Executive Catering Management System - Main Entry Point
 */
public class CateringManagementSystem extends JFrame {
    private static final long serialVersionUID = 1L;

    private DataManager dataManager;

    private JTabbedPane tabbedPane;
    private OrderManagementPanel orderPanel;
    private CustomerManagementPanel customerPanel;
    private MenuManagementPanel menuPanel;
    private AnalyticsPanel analyticsPanel;
    private HeaderPanel headerPanel;

    // High-Contrast Theme Palette
    public static final Color BG_DARK = new Color(15, 23, 42);          // Slate 900
    public static final Color PANEL_BG = new Color(30, 41, 59);         // Slate 800
    public static final Color CARD_BG = new Color(51, 65, 85);          // Slate 700
    public static final Color INPUT_BG = new Color(15, 23, 42);         // Slate 900 Input BG

    public static final Color ACCENT_BLUE = new Color(37, 99, 235);     // Blue 600
    public static final Color ACCENT_BLUE_BORDER = new Color(96, 165, 250); // Blue 400 Border
    public static final Color ACCENT_GREEN = new Color(22, 163, 74);    // Emerald 600
    public static final Color ACCENT_RED = new Color(220, 38, 38);      // Red 600
    public static final Color ACCENT_AMBER = new Color(217, 119, 6);    // Amber 600
    public static final Color ACCENT_PURPLE = new Color(147, 51, 234);  // Purple 600

    public static final Color TEXT_WHITE = new Color(255, 255, 255);    // Pure White
    public static final Color TEXT_YELLOW = new Color(254, 240, 138);   // Soft Yellow
    public static final Color TEXT_MUTED = new Color(203, 213, 225);    // Slate 300
    public static final Color BORDER_COLOR = new Color(100, 116, 139);  // Slate 500

    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("₹#,##0.00");

    public CateringManagementSystem() {
        setTitle("Catering Management System - Enterprise Edition (Live PDF Sync)");
        setSize(1260, 860);
        setMinimumSize(new Dimension(1020, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        dataManager = new DataManager();

        headerPanel = new HeaderPanel(dataManager);
        add(headerPanel, BorderLayout.NORTH);

        orderPanel = new OrderManagementPanel(this, dataManager);
        customerPanel = new CustomerManagementPanel(this, dataManager);
        menuPanel = new MenuManagementPanel(this, dataManager);
        analyticsPanel = new AnalyticsPanel(dataManager);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(TEXT_WHITE);
        tabbedPane.setUI(new CustomTabUI());

        tabbedPane.addTab("  🛒 New Booking & 300+ Menu  ", orderPanel);
        tabbedPane.addTab("  📋 Customer Orders & Payment Settle  ", customerPanel);
        tabbedPane.addTab("  👨‍🍳 Menu Master  ", menuPanel);
        tabbedPane.addTab("  📊 Executive Analytics  ", analyticsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        refreshAllPanels();
        setVisible(true);
    }

    public void refreshAllPanels() {
        if (headerPanel != null) headerPanel.updateMetrics();
        if (orderPanel != null) orderPanel.refreshMenuList();
        if (customerPanel != null) customerPanel.refreshOrderTable();
        if (menuPanel != null) menuPanel.refreshMenuTable();
        if (analyticsPanel != null) analyticsPanel.updateAnalytics();
    }

    public void switchToCustomerManagement() {
        tabbedPane.setSelectedIndex(1);
        refreshAllPanels();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CateringManagementSystem());
    }
}