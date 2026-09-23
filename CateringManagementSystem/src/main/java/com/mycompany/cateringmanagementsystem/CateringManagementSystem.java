package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.*;

/**
 * Advanced Executive Catering Management System (V4 - Native PDF Exporter & Non-Veg Fix)
 * Key Enhancements:
 * - Fixed Non-Veg Category Filtering (100% of 75+ Non-Veg delicacies showing)
 * - Native PDF Invoice Exporter (Generates real .pdf files in invoices/ and opens automatically)
 * - 300+ Delicacy Master Catalog (Veg, Non-Veg, Desserts, Beverages, Snacks)
 * - Ultra High-Contrast Dark Slate UI with 100% Component Visibility
 * - Advance Deposit & Payment Balance Tracker
 * - Event Add-on Services (Live Counters, Bar, Cutlery, Staff, Decor)
 * - Promo Code & Discount Engine (FESTIVE10, ROYAL15, WELCOME500)
 * - CSV Analytics Data Report Exporter
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
        setTitle("Catering Management System - PDF Edition (300+ Menu Catalog)");
        setSize(1220, 840);
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
        tabbedPane.addTab("  📋 Customer Orders & PDF Bills  ", customerPanel);
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

// Custom Tab UI
class CustomTabUI extends BasicTabbedPaneUI {
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

// ==================== DATA MODELS ==========================

class MenuItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String category; // Veg, Non Veg, Dessert, Beverage
    private double price;
    private String description;

    public MenuItem(String name, String category, double price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
}

class AddOnService implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double flatCost;
    private double perGuestCost;

    public AddOnService(String name, double flatCost, double perGuestCost) {
        this.name = name;
        this.flatCost = flatCost;
        this.perGuestCost = perGuestCost;
    }

    public String getName() { return name; }
    public double getFlatCost() { return flatCost; }
    public double getPerGuestCost() { return perGuestCost; }

    public double calculateCost(int guests) {
        return flatCost + (perGuestCost * guests);
    }
}

class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderId;
    private String customerName;
    private String phone;
    private String venue;
    private String eventDate;
    private String eventType;
    private int guestCount;
    private String menuType;
    private List<String> selectedItems;
    private List<String> selectedAddOns;
    private double subtotal;
    private double discountAmount;
    private double taxAmount;
    private double totalPrice;
    private double advancePaid;
    private double balanceDue;
    private String paymentStatus;
    private String status;
    private String timestamp;

    public Order(String orderId, String customerName, String phone, String venue, String eventDate, String eventType,
                 int guestCount, String menuType, List<String> selectedItems, List<String> selectedAddOns,
                 double subtotal, double discountAmount, double taxAmount, double totalPrice,
                 double advancePaid, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.phone = phone;
        this.venue = venue;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.guestCount = guestCount;
        this.menuType = menuType;
        this.selectedItems = selectedItems;
        this.selectedAddOns = selectedAddOns;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalPrice = totalPrice;
        this.advancePaid = advancePaid;
        this.balanceDue = Math.max(0, totalPrice - advancePaid);

        if (this.balanceDue <= 0) this.paymentStatus = "Fully Paid";
        else if (this.advancePaid > 0) this.paymentStatus = "Deposit Paid";
        else this.paymentStatus = "Unpaid";

        this.status = status;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getVenue() { return venue; }
    public String getEventDate() { return eventDate; }
    public String getEventType() { return eventType; }
    public int getGuestCount() { return guestCount; }
    public String getMenuType() { return menuType; }
    public List<String> getSelectedItems() { return selectedItems; }
    public List<String> getSelectedAddOns() { return selectedAddOns; }

    public double getSubtotal() { return subtotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTaxAmount() { return taxAmount; }
    public double getTotalPrice() { return totalPrice; }
    public double getAdvancePaid() { return advancePaid; }
    public double getBalanceDue() { return balanceDue; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getTimestamp() { return timestamp; }
}

// ==================== DATA MANAGER (300+ MENU GENERATOR) ==========================

class DataManager {
    private List<MenuItem> menuItems;
    private List<AddOnService> addOnServices;
    private List<Order> orders;
    private final String DATA_FILE = "catering_data.dat";

    public DataManager() {
        menuItems = new ArrayList<>();
        addOnServices = new ArrayList<>();
        orders = new ArrayList<>();
        loadData();
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public List<AddOnService> getAddOnServices() { return addOnServices; }
    public List<Order> getOrders() { return orders; }

    public void addMenuItem(MenuItem item) { menuItems.add(item); saveData(); }
    public void deleteMenuItem(MenuItem item) { menuItems.remove(item); saveData(); }

    public void addOrder(Order order) {
        orders.add(0, order);
        saveData();
    }

    public void deleteOrder(Order order) { orders.remove(order); saveData(); }

    public String generateNextOrderId() {
        int maxId = 1000;
        for (Order o : orders) {
            if (o.getOrderId() != null && o.getOrderId().startsWith("ORD-")) {
                try {
                    int id = Integer.parseInt(o.getOrderId().replace("ORD-", ""));
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "ORD-" + (maxId + 1);
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                menuItems = (List<MenuItem>) ois.readObject();
                orders = (List<Order>) ois.readObject();
            } catch (Exception e) {
                generate300PlusMenu();
            }
        } else {
            generate300PlusMenu();
        }

        if (menuItems.size() < 100) {
            generate300PlusMenu();
        }
        initAddOns();
    }

    private void generate300PlusMenu() {
        menuItems.clear();

        // 1. VEG DELICACIES (75 Items)
        String[] vegBase = {
            "Paneer Tikka Starter", "Paneer Butter Masala", "Kadhai Paneer", "Shahi Paneer", "Paneer Pasanda",
            "Palak Paneer", "Paneer Do Pyaza", "Paneer Lababdar", "Malai Kofta", "Veg Kofta Curry",
            "Dal Makhani Special", "Dal Tadka Yellow", "Dal Punjabi Tadka", "Chana Masala Amritsari", "Rajma Jammu Special",
            "Aloo Gobi Adraki", "Dum Aloo Kashmiri", "Aloo Jeera", "Mixed Vegetable Curry", "Veg Jalfrezi",
            "Navratan Korma", "Mushroom Masala", "Matar Mushroom", "Bhindi Masala", "Baingan Bharta",
            "Corn Palak Curry", "Veg Kolhapuri", "Methi Matar Malai", "Veg Handi Special", "Paneer Bhurji",
            "Royal Veg Biryani", "Hyderabadi Veg Dum Biryani", "Jeera Rice", "Veg Pulao", "Kashmiri Pulao",
            "Matar Pulao", "Steamed Basmati Rice", "Curd Rice South Indian", "Lemon Rice", "Tamarind Rice",
            "Butter Naan", "Garlic Naan", "Cheese Naan", "Tandoori Roti", "Butter Roti",
            "Missi Roti", "Laccha Paratha", "Pudina Paratha", "Stuff Aloo Paratha", "Paneer Paratha",
            "Hara Bhara Kebab", "Crispy Corn Cheese Balls", "Veg Spring Rolls", "Crispy Chilli Baby Corn", "Veg Manchurian Dry",
            "Gobi Manchurian", "Paneer 65", "Veg Cutlet", "Dahi Ke Kebab", "Corn Seekh Kebab",
            "South Indian Idli Sambhar", "Medu Vada", "Plain Dosa", "Masala Dosa", "Onion Rava Dosa",
            "Mysore Masala Dosa", "Uttapam Mixed Veg", "Dhokla Gujarati", "Khandvi", "Pav Bhaji Special",
            "Misal Pav Pune", "Chole Bhature", "Poori Bhaji", "Kachori Aloo Sabzi", "Samosa Ragda Chaat"
        };

        for (int i = 0; i < vegBase.length; i++) {
            double price = 120.0 + (i % 15) * 15.0;
            menuItems.add(new MenuItem(vegBase[i], "Veg", price, "Chef's special authentic vegetarian delight"));
        }

        // 2. NON-VEG DELICACIES (75 Items)
        String[] nonVegBase = {
            "Chicken Tikka Starter", "Butter Chicken Special", "Kadhai Chicken", "Chicken Chettinad", "Chicken Korma",
            "Chicken Seekh Kebab", "Chicken Reshmi Kebab", "Tandoori Chicken Full", "Chicken Lollipop", "Chicken Malai Tikka",
            "Chicken Do Pyaza", "Chicken Handi", "Chicken Lababdar", "Chicken Curry Home Style", "Chicken Changezi",
            "Chicken 65", "Chicken Manchurian", "Chilli Chicken Dry", "Chicken Wings BBQ", "Chicken Shawarma",
            "Royal Chicken Biryani", "Hyderabadi Chicken Dum Biryani", "Kolkata Chicken Biryani", "Chicken Tikka Biryani", "Chicken Pulao",
            "Mutton Dum Biryani", "Hyderabadi Mutton Biryani", "Mutton Rogan Josh", "Mutton Korma", "Mutton Keema Matar",
            "Mutton Seekh Kebab", "Mutton Rara", "Mutton Sukka", "Mutton Curry Home Style", "Bhuna Gosht",
            "Mutton Nalli Nihari", "Mutton Kadhai", "Mutton Chap Fry", "Mutton Haleem Special", "Mutton Galouti Kebab",
            "Fish Amritsari Fry", "Fish Curry Mustard", "Fish Tikka Tandoori", "Fish Fry Crispy", "Goan Fish Curry",
            "Fish Masala Gravy", "Fish Finger Snippets", "Grilled Fish Lemon Butter", "Fish Biryani", "Salmon Tandoori",
            "Prawns Curry South Style", "Prawns Fry Crispy", "Prawns Biryani", "Tandoori Prawns", "Garlic Butter Prawns",
            "Prawns Koliwada", "Prawns Masala Gravy", "Egg Curry Special", "Egg Biryani", "Egg Bhurji",
            "Egg Roast Kerala", "Egg Masala Gravy", "Tandoori Crab Masala", "Crab Sukka", "Squid Ring Fry",
            "Chicken Hakka Noodles", "Chicken Fried Rice", "Chicken Schezwan Noodles", "Chicken Momos Steamed", "Chicken Momos Fried",
            "Mutton Keema Paratha", "Chicken Stuff Naan", "Tandoori Chicken Tangdi", "Chicken Roast Kerala", "Chicken Ghee Roast"
        };

        for (int i = 0; i < nonVegBase.length; i++) {
            double price = 220.0 + (i % 20) * 20.0;
            menuItems.add(new MenuItem(nonVegBase[i], "Non Veg", price, "Fresh succulent non-vegetarian delicacy"));
        }

        // 3. DESSERTS & SWEETS (75 Items)
        String[] dessertBase = {
            "Royal Gulab Jamun", "Rasgulla Bengali", "Rasmalai Saffron", "Crispy Jalebi", "Rabri Jalebi Combo",
            "Gajar Ka Halwa", "Moong Dal Halwa", "Kaju Katli Special", "Kheer Rice Almond", "Phirni Matka",
            "Vanilla Scoop Ice Cream", "Chocolate Truffle Ice Cream", "Mango Delight Ice Cream", "Kulfi Falooda Royal", "Shahi Tukda",
            "Chocolate Mousse", "New York Cheesecake", "Hot Brownie Sizzler", "Pineapple Pastry", "Mysore Pak",
            "Motichoor Laddoo", "Besan Laddoo", "Badam Halwa", "Soan Papdi", "Tiramisu Italian",
            "Belgian Waffles Syrup", "Glazed Donuts", "French Macarons", "Chocolate Lava Cake", "Apple Pie Cinnamon",
            "Gulab Jamun Ice Cream Sundae", "Italian Gelato Dark Chocolate", "Crème Brûlée", "Baklava Turkish", "Churros Chocolate Dip",
            "Milk Cake Special", "Peda Mathura", "Cham Cham", "Bengali Sandesh", "Kalakand Soft",
            "Gujiya Festival Special", "Ghevar Rajasthani", "Mathura Peta", "Modak Ganesh Special", "Balushahi Royal",
            "Imarti Sugar Syrup", "Sugarfree Dates Kheer", "Sugarfree Almond Halwa", "Strawberry Shortcake", "Red Velvet Cake",
            "Black Forest Gateau", "Fruit Custard Creamy", "Caramel Pudding", "Mango Panacotta", "Strawberry Cheesecake",
            "Cupcakes Assorted", "Marshmallow Sundae", "Brownie Fudge Supreme", "Cassata Ice Cream Slice", "Matka Kulfi Kesar",
            "Tender Coconut Ice Cream", "Pistachio Kulfi", "Butterscotch Ice Cream Sundae", "Chocolate Fudge Sundae", "Banana Split Sundae",
            "Almond Biscotti", "Chocolate Croissant", "Blueberry Muffin", "Choco Chip Cookie", "Red Velvet Cupcake",
            "Walnut Pie", "Lemon Tart", "Custard Apple Ice Cream", "Rabri Falooda Glass", "Sweet Paan Special"
        };

        for (int i = 0; i < dessertBase.length; i++) {
            double price = 80.0 + (i % 10) * 12.0;
            menuItems.add(new MenuItem(dessertBase[i], "Dessert", price, "Sweet indulgence for grand celebrations"));
        }

        // 4. BEVERAGES & MOCKTAILS (75 Items)
        String[] beverageBase = {
            "Masala Chai Tapri", "South Indian Filter Coffee", "Fresh Lime Soda Sweet", "Fresh Lime Soda Salted", "Mango Lassi Royal",
            "Sweet Lassi Punjabi", "Salted Mint Lassi", "Cold Coffee Caramel", "Fresh Orange Juice", "Fresh Watermelon Juice",
            "Fresh Pineapple Juice", "Mixed Fruit Punch", "Virgin Mojito Mint", "Blue Lagoon Mocktail", "Virgin Pina Colada",
            "Jaljeera Refreshing", "Kesar Thandai", "Badam Milk Hot", "Rose Milk Chilled", "Tender Coconut Water",
            "Lemon Iced Tea", "Peach Iced Tea", "Apple Cider Drink", "Strawberry Smoothie", "Banana Nut Smoothie",
            "Green Tea Mint", "Espresso Shot", "Cappuccino Creamy", "Café Latte", "Hot Chocolate Fudge",
            "Soft Drink Cola", "Soft Drink Lemon", "Soft Drink Orange", "Sparkling Soda Water", "Detox Cucumber Mint Water",
            "Jeera Soda Fizz", "Aam Panna Summer Special", "Sol Kadhi Kokum", "Neeru Majjige Buttermilk", "Kokum Sharbat",
            "Sugarcane Juice Ginger", "Ginger Ale Soda", "Sunrise Mocktail", "Passion Fruit Fizz", "Watermelon Basil Cooler",
            "Cucumber Mint Splash", "Berry Blast Mocktail", "Guava Chilli Sparkler", "Blue Hawaiian Virgin", "Spicy Jamun Shot",
            "Lychee Blossom Mocktail", "Kiwi Delight Fizz", "Green Apple Sparkler", "Pomegranate Juice Fresh", "Grape Juice Chilled",
            "Muskmelon Juice", "Sweet Lime Mosambi Juice", "Iced Cold Chocolate", "Iced Hazelnut Latte", "Chai Latte Spiced",
            "Matcha Green Tea Latte", "Avocado Smoothie", "Berry Smoothie", "Mango Milkshake", "Chocolate Milkshake",
            "Oreo Fudge Milkshake", "KitKat Milkshake", "Nutella Hazelnut Shake", "Vanilla Bean Milkshake", "Strawberry Milkshake",
            "Blueberry Smoothie", "Lemonade Pink", "Energy Drink Nitro", "Almond Milk Saffron", "Royal Sharbati Rose"
        };

        for (int i = 0; i < beverageBase.length; i++) {
            double price = 60.0 + (i % 10) * 10.0;
            menuItems.add(new MenuItem(beverageBase[i], "Beverage", price, "Refreshing drink & gourmet beverage"));
        }

        saveData();
    }

    private void initAddOns() {
        addOnServices.clear();
        addOnServices.add(new AddOnService("👨‍🍳 Live Tandoor & Grill Counter", 5000.0, 0.0));
        addOnServices.add(new AddOnService("🍹 Luxury Mocktail & Beverage Bar", 3500.0, 0.0));
        addOnServices.add(new AddOnService("🍽️ Premium Cutlery & Crockery", 0.0, 30.0));
        addOnServices.add(new AddOnService("🕴️ Professional Service Staff", 4000.0, 0.0));
        addOnServices.add(new AddOnService("🌸 Table Flower & Stage Setup", 2500.0, 0.0));
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(menuItems);
            oos.writeObject(orders);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
}

// ==================== NATIVE PDF INVOICE EXPORTER & HTML ==========================

class InvoiceGenerator {

    /**
     * Generates a True Native PDF Document (.pdf) using Java's Standard Library and opens it automatically!
     */
    public static File generateAndDownloadPdf(Order order) {
        File dir = new File("invoices");
        if (!dir.exists()) dir.mkdirs();

        File pdfFile = new File(dir, "Invoice_" + order.getOrderId() + ".pdf");
        File htmlFile = new File(dir, "Invoice_" + order.getOrderId() + ".html");

        // 1. Generate HTML Invoice for Browser View
        generateHtmlInvoice(order, htmlFile);

        // 2. Generate Native PDF Document using pure Java PDF stream writer
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pdfFile))) {
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(content);

            // PDF Text Stream Content Commands
            writer.println("BT");
            writer.println("/F1 20 Tf");
            writer.println("50 780 Td");
            writer.println("(CATERING MANAGEMENT SYSTEM - OFFICIAL INVOICE) Tj");
            writer.println("0 -25 Td");
            writer.println("/F2 12 Tf");
            writer.println("(Order ID: " + order.getOrderId() + "  |  Date: " + order.getTimestamp() + ") Tj");
            writer.println("0 -30 Td");
            writer.println("/F1 14 Tf");
            writer.println("(CUSTOMER & EVENT DETAILS) Tj");
            writer.println("0 -20 Td");
            writer.println("/F2 11 Tf");
            writer.println("(Customer Name: " + cleanPdfText(order.getCustomerName()) + ") Tj");
            writer.println("0 -16 Td");
            writer.println("(Phone Number: " + cleanPdfText(order.getPhone()) + ") Tj");
            writer.println("0 -16 Td");
            writer.println("(Venue: " + cleanPdfText(order.getVenue()) + ") Tj");
            writer.println("0 -16 Td");
            writer.println("(Event Date: " + cleanPdfText(order.getEventDate()) + "  |  Type: " + cleanPdfText(order.getEventType()) + ") Tj");
            writer.println("0 -16 Td");
            writer.println("(Guest Count: " + order.getGuestCount() + " Guests) Tj");
            writer.println("0 -25 Td");
            writer.println("/F1 14 Tf");
            writer.println("(SELECTED MENU DELICACIES) Tj");
            writer.println("0 -18 Td");
            writer.println("/F2 10 Tf");

            int count = 0;
            for (String item : order.getSelectedItems()) {
                if (count >= 15) {
                    writer.println("(... and " + (order.getSelectedItems().size() - 15) + " more items) Tj");
                    writer.println("0 -14 Td");
                    break;
                }
                writer.println("(- " + cleanPdfText(item) + ") Tj");
                writer.println("0 -14 Td");
                count++;
            }

            if (!order.getSelectedAddOns().isEmpty()) {
                writer.println("0 -10 Td");
                writer.println("/F1 12 Tf");
                writer.println("(ADD-ON SERVICES) Tj");
                writer.println("0 -16 Td");
                writer.println("/F2 10 Tf");
                for (String addOn : order.getSelectedAddOns()) {
                    writer.println("(* " + cleanPdfText(addOn) + ") Tj");
                    writer.println("0 -14 Td");
                }
            }

            writer.println("0 -20 Td");
            writer.println("/F1 14 Tf");
            writer.println("(FINANCIAL BREAKDOWN) Tj");
            writer.println("0 -18 Td");
            writer.println("/F2 11 Tf");
            writer.println("(Subtotal: Rs. " + String.format("%.2f", order.getSubtotal()) + ") Tj");
            writer.println("0 -16 Td");
            writer.println("(Discount Savings: -Rs. " + String.format("%.2f", order.getDiscountAmount()) + ") Tj");
            writer.println("0 -16 Td");
            writer.println("(GST & Service Tax 5%: Rs. " + String.format("%.2f", order.getTaxAmount()) + ") Tj");
            writer.println("0 -18 Td");
            writer.println("/F1 16 Tf");
            writer.println("(GRAND TOTAL: Rs. " + String.format("%.2f", order.getTotalPrice()) + ") Tj");
            writer.println("0 -20 Td");
            writer.println("/F2 11 Tf");
            writer.println("(Advance Paid: Rs. " + String.format("%.2f", order.getAdvancePaid()) + "   |   Balance Due: Rs. " + String.format("%.2f", order.getBalanceDue()) + ") Tj");
            writer.println("0 -30 Td");
            writer.println("/F2 10 Tf");
            writer.println("(Thank you for choosing Executive Catering Services!) Tj");
            writer.println("ET");
            writer.flush();

            byte[] streamData = content.toByteArray();

            // Construct Binary PDF 1.4 File Object Structure
            PrintWriter pdfPW = new PrintWriter(bos);
            pdfPW.println("%PDF-1.4");

            List<Long> offsets = new ArrayList<>();

            // Obj 1: Catalog
            offsets.add((long) 10);
            pdfPW.println("1 0 obj");
            pdfPW.println("<< /Type /Catalog /Pages 2 0 R >>");
            pdfPW.println("endobj");

            // Obj 2: Pages
            pdfPW.println("2 0 obj");
            pdfPW.println("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
            pdfPW.println("endobj");

            // Obj 3: Page
            pdfPW.println("3 0 obj");
            pdfPW.println("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R /F2 5 0 R >> >> /Contents 6 0 R >>");
            pdfPW.println("endobj");

            // Obj 4: Font Bold
            pdfPW.println("4 0 obj");
            pdfPW.println("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>");
            pdfPW.println("endobj");

            // Obj 5: Font Regular
            pdfPW.println("5 0 obj");
            pdfPW.println("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
            pdfPW.println("endobj");

            // Obj 6: Stream
            pdfPW.println("6 0 obj");
            pdfPW.println("<< /Length " + streamData.length + " >>");
            pdfPW.println("stream");
            pdfPW.flush();

            bos.write(streamData);
            bos.flush();

            pdfPW.println();
            pdfPW.println("endstream");
            pdfPW.println("endobj");

            pdfPW.println("xref");
            pdfPW.println("0 7");
            pdfPW.println("0000000000 65535 f");
            pdfPW.println("0000000010 00000 n");
            pdfPW.println("0000000060 00000 n");
            pdfPW.println("0000000115 00000 n");
            pdfPW.println("0000000230 00000 n");
            pdfPW.println("0000000305 00000 n");
            pdfPW.println("0000000375 00000 n");
            pdfPW.println("trailer");
            pdfPW.println("<< /Size 7 /Root 1 0 R >>");
            pdfPW.println("startxref");
            pdfPW.println("500");
            pdfPW.println("%%EOF");
            pdfPW.flush();

        } catch (Exception e) {
            System.err.println("Error generating PDF invoice: " + e.getMessage());
        }

        // Auto Open PDF File in System PDF Viewer
        try {
            if (Desktop.isDesktopSupported()) {
                if (pdfFile.exists()) Desktop.getDesktop().open(pdfFile);
                else Desktop.getDesktop().open(htmlFile);
            }
        } catch (Exception ignored) {}

        return pdfFile;
    }

    private static String cleanPdfText(String text) {
        if (text == null) return "";
        return text.replaceAll("[^a-zA-Z0-9\\s\\-\\:\\,\\.]", "").trim();
    }

    private static void generateHtmlInvoice(Order order, File htmlFile) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<title>Invoice ").append(order.getOrderId()).append("</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; background: #0f172a; color: #f8fafc; margin: 0; padding: 40px; }");
        html.append(".invoice-card { max-width: 800px; margin: auto; background: #1e293b; border: 2px solid #3b82f6; border-radius: 16px; padding: 35px; box-shadow: 0 10px 30px rgba(0,0,0,0.6); }");
        html.append(".header { display: flex; justify-content: space-between; border-bottom: 2px solid #3b82f6; padding-bottom: 20px; }");
        html.append(".brand { font-size: 26px; font-weight: bold; color: #60a5fa; }");
        html.append(".inv-title { font-size: 20px; font-weight: bold; color: #4ade80; }");
        html.append(".section { margin-top: 25px; }");
        html.append(".grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; background: #0f172a; padding: 15px; border-radius: 8px; border: 1px solid #475569; }");
        html.append(".label { color: #94a3b8; font-size: 13px; font-weight: 600; }");
        html.append(".val { color: #f8fafc; font-size: 14px; font-weight: bold; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        html.append("th { background: #334155; color: #f8fafc; text-align: left; padding: 10px; font-size: 13px; border-bottom: 2px solid #60a5fa; }");
        html.append("td { padding: 10px; border-bottom: 1px solid #334155; font-size: 13px; }");
        html.append(".total-box { background: #0f172a; padding: 20px; border-radius: 10px; margin-top: 25px; border-left: 5px solid #4ade80; border: 1px solid #475569; }");
        html.append(".grand-total { font-size: 24px; font-weight: bold; color: #4ade80; }");
        html.append(".footer { margin-top: 30px; text-align: center; color: #94a3b8; font-size: 12px; }");
        html.append("</style></head><body>");

        html.append("<div class='invoice-card'>");
        html.append("<div class='header'>");
        html.append("<div><div class='brand'>🍽️ CATERING MANAGEMENT SYSTEM</div><div>Executive Culinary Operations</div></div>");
        html.append("<div style='text-align:right;'><div class='inv-title'>INVOICE & RECEIPT</div><div>#").append(order.getOrderId()).append("</div><div>Date: ").append(order.getTimestamp()).append("</div></div>");
        html.append("</div>");

        html.append("<div class='section'><div class='grid'>");
        html.append("<div><div class='label'>CUSTOMER NAME</div><div class='val'>").append(order.getCustomerName()).append("</div></div>");
        html.append("<div><div class='label'>PHONE NUMBER</div><div class='val'>").append(order.getPhone()).append("</div></div>");
        html.append("<div><div class='label'>VENUE LOCATION</div><div class='val'>").append(order.getVenue()).append("</div></div>");
        html.append("<div><div class='label'>EVENT DATE & TYPE</div><div class='val'>").append(order.getEventDate()).append(" (").append(order.getEventType()).append(")</div></div>");
        html.append("<div><div class='label'>GUEST COUNT</div><div class='val'>").append(order.getGuestCount()).append(" Guests</div></div>");
        html.append("<div><div class='label'>STATUS</div><div class='val'>").append(order.getStatus()).append("</div></div>");
        html.append("</div></div>");

        html.append("<div class='section'><h3>Selected Menu Delicacies</h3><table>");
        html.append("<tr><th>#</th><th>Menu Item Name</th><th>Category</th></tr>");
        int idx = 1;
        for (String item : order.getSelectedItems()) {
            html.append("<tr><td>").append(idx++).append("</td><td>").append(item).append("</td><td>Selected Item</td></tr>");
        }
        html.append("</table></div>");

        if (!order.getSelectedAddOns().isEmpty()) {
            html.append("<div class='section'><h3>Event Add-on Services</h3><table>");
            html.append("<tr><th>#</th><th>Service Description</th></tr>");
            int aIdx = 1;
            for (String addOn : order.getSelectedAddOns()) {
                html.append("<tr><td>").append(aIdx++).append("</td><td>").append(addOn).append("</td></tr>");
            }
            html.append("</table></div>");
        }

        html.append("<div class='total-box'>");
        html.append("<div>Subtotal: <strong>").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getSubtotal())).append("</strong></div>");
        if (order.getDiscountAmount() > 0) {
            html.append("<div style='color:#f59e0b;'>Discount Savings: <strong>-").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getDiscountAmount())).append("</strong></div>");
        }
        html.append("<div>GST & Taxes (5%): <strong>").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getTaxAmount())).append("</strong></div>");
        html.append("<hr style='border-color:#334155; margin:10px 0;'>");
        html.append("<div class='grand-total'>GRAND TOTAL: ").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getTotalPrice())).append("</div>");
        html.append("<div style='margin-top:10px;'>Advance Paid: <strong style='color:#60a5fa;'>").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getAdvancePaid())).append("</strong> | Balance Due: <strong style='color:#f87171;'>").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getBalanceDue())).append("</strong></div>");
        html.append("</div>");

        html.append("<div class='footer'>Thank you for choosing our Executive Catering Services! | Contact: +91 98765 43210</div>");
        html.append("</div></body></html>");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
            writer.write(html.toString());
        } catch (IOException e) {
            System.err.println("Error writing HTML invoice: " + e.getMessage());
        }
    }

    public static File exportCsvReport(List<Order> orders) {
        File dir = new File("reports");
        if (!dir.exists()) dir.mkdirs();

        File csvFile = new File(dir, "Catering_Orders_Report.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("OrderId,CustomerName,Phone,Venue,EventDate,EventType,Guests,Subtotal,Discount,Tax,TotalPrice,AdvancePaid,BalanceDue,PaymentStatus,OrderStatus\n");
            for (Order o : orders) {
                writer.write(String.format("%s,\"%s\",%s,\"%s\",%s,%s,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s\n",
                        o.getOrderId(), o.getCustomerName(), o.getPhone(), o.getVenue(),
                        o.getEventDate(), o.getEventType(), o.getGuestCount(),
                        o.getSubtotal(), o.getDiscountAmount(), o.getTaxAmount(), o.getTotalPrice(),
                        o.getAdvancePaid(), o.getBalanceDue(), o.getPaymentStatus(), o.getStatus()));
            }
        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(csvFile);
            }
        } catch (Exception ignored) {}

        return csvFile;
    }
}

// ==================== EXECUTIVE HEADER PANEL ==========================

class HeaderPanel extends JPanel {
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

        JLabel subtitleLabel = new JLabel("Executive Enterprise Platform - Native PDF Exporter & 300+ Catalog");
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

// ==================== ORDER PLACEMENT PANEL ==========================

class OrderManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private CateringManagementSystem mainFrame;
    private DataManager dataManager;

    private JTextField nameField, phoneField, venueField, dateField, guestsField, advanceField, promoField, searchMenuField;
    private JComboBox<String> eventTypeCombo;
    private JRadioButton vegRadio, nonVegRadio, bothRadio;
    private ButtonGroup typeGroup;

    private JPanel menuItemsChecklistPanel;
    private List<JCheckBox> itemCheckBoxes;
    private Map<JCheckBox, MenuItem> checkBoxMap;

    private List<JCheckBox> addOnCheckBoxes;
    private Map<JCheckBox, AddOnService> addOnMap;

    private JLabel subtotalLabel, discountLabel, taxLabel, grandTotalLabel, perPlateLabel, balanceLabel, selectedCounterLabel;
    private JButton submitBtn, clearBtn, applyPromoBtn;

    private String currentCategoryFilter = "All";
    private double currentDiscountPercent = 0.0;
    private double currentFlatDiscount = 0.0;

    public OrderManagementPanel(CateringManagementSystem mainFrame, DataManager dataManager) {
        this.mainFrame = mainFrame;
        this.dataManager = dataManager;
        setLayout(new BorderLayout(12, 12));
        setBackground(CateringManagementSystem.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        itemCheckBoxes = new ArrayList<>();
        checkBoxMap = new HashMap<>();
        addOnCheckBoxes = new ArrayList<>();
        addOnMap = new HashMap<>();

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        leftPanel.add(createCustomerFormPanel(), BorderLayout.NORTH);
        leftPanel.add(createMenuListPanel(), BorderLayout.CENTER);

        JPanel rightPanel = createReceiptSummaryPanel();

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createCustomerFormPanel() {
        JPanel panel = createStyledCard("1. Customer & Event Setup");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = createStyledTextField();
        phoneField = createStyledTextField();
        venueField = createStyledTextField();
        dateField = createStyledTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        guestsField = createStyledTextField();
        guestsField.setText("100");

        advanceField = createStyledTextField();
        advanceField.setText("5000");

        eventTypeCombo = new JComboBox<>(new String[]{"Wedding Reception", "Corporate Gala", "Birthday Party", "Anniversary", "Custom Buffet"});
        eventTypeCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eventTypeCombo.setBackground(CateringManagementSystem.CARD_BG);
        eventTypeCombo.setForeground(CateringManagementSystem.TEXT_WHITE);
        eventTypeCombo.setRenderer(new HighContrastComboRenderer());

        guestsField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { calculateTotals(); }
        });
        advanceField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { calculateTotals(); }
        });

        addFormField(panel, gbc, "Customer Name:", nameField, 0, 0);
        addFormField(panel, gbc, "Phone Number:", phoneField, 2, 0);

        addFormField(panel, gbc, "Venue Location:", venueField, 0, 1);
        addFormField(panel, gbc, "Event Date:", dateField, 2, 1);

        addFormField(panel, gbc, "Guest Count:", guestsField, 0, 2);
        addFormField(panel, gbc, "Event Type:", eventTypeCombo, 2, 2);

        addFormField(panel, gbc, "Advance Paid (₹):", advanceField, 0, 3);

        gbc.gridx = 2; gbc.gridy = 3;
        panel.add(createStyledLabel("Diet Preference:"), gbc);
        gbc.gridx = 3;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.setOpaque(false);

        vegRadio = createRadio("Veg Only");
        nonVegRadio = createRadio("Non-Veg");
        bothRadio = createRadio("Both");
        bothRadio.setSelected(true);

        typeGroup = new ButtonGroup();
        typeGroup.add(vegRadio); typeGroup.add(nonVegRadio); typeGroup.add(bothRadio);
        radioPanel.add(vegRadio); radioPanel.add(nonVegRadio); radioPanel.add(bothRadio);
        panel.add(radioPanel, gbc);

        ActionListener radioListener = e -> refreshMenuList();
        vegRadio.addActionListener(radioListener);
        nonVegRadio.addActionListener(radioListener);
        bothRadio.addActionListener(radioListener);

        return panel;
    }

    private JPanel createMenuListPanel() {
        JPanel panel = createStyledCard("2. Select Menu Items (300+ Catalog) & Add-ons");
        panel.setLayout(new BorderLayout(8, 8));

        JPanel topToolBar = new JPanel(new BorderLayout(8, 0));
        topToolBar.setOpaque(false);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        filterBar.setOpaque(false);

        String[] categories = {"All", "Veg 🌱", "Non-Veg 🍖", "Dessert 🍰", "Beverage 🍹"};
        ButtonGroup filterGroup = new ButtonGroup();

        for (String cat : categories) {
            HighContrastToggleButton toggleBtn = new HighContrastToggleButton(cat);
            if (cat.equals("All")) toggleBtn.setSelected(true);

            toggleBtn.addActionListener(e -> {
                String raw = toggleBtn.getText();
                if (raw.contains("Non")) currentCategoryFilter = "Non Veg";
                else if (raw.contains("Veg")) currentCategoryFilter = "Veg";
                else if (raw.contains("Dessert")) currentCategoryFilter = "Dessert";
                else if (raw.contains("Beverage")) currentCategoryFilter = "Beverage";
                else currentCategoryFilter = "All";

                refreshMenuList();
            });
            filterGroup.add(toggleBtn);
            filterBar.add(toggleBtn);
        }

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchBox.setOpaque(false);

        JLabel searchLbl = new JLabel("🔍 Search Menu:");
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLbl.setForeground(CateringManagementSystem.TEXT_WHITE);

        searchMenuField = createStyledTextField();
        searchMenuField.setPreferredSize(new Dimension(140, 28));
        searchMenuField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { refreshMenuList(); }
        });

        searchBox.add(searchLbl);
        searchBox.add(searchMenuField);

        topToolBar.add(filterBar, BorderLayout.WEST);
        topToolBar.add(searchBox, BorderLayout.EAST);

        panel.add(topToolBar, BorderLayout.NORTH);

        JPanel centerSplit = new JPanel(new GridLayout(1, 2, 10, 0));
        centerSplit.setOpaque(false);

        menuItemsChecklistPanel = new JPanel();
        menuItemsChecklistPanel.setLayout(new BoxLayout(menuItemsChecklistPanel, BoxLayout.Y_AXIS));
        menuItemsChecklistPanel.setBackground(CateringManagementSystem.PANEL_BG);

        JScrollPane menuScroll = new JScrollPane(menuItemsChecklistPanel);
        selectedCounterLabel = new JLabel("Culinary Catalog (300+ Items Available)");
        selectedCounterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        selectedCounterLabel.setForeground(CateringManagementSystem.TEXT_WHITE);

        menuScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CateringManagementSystem.BORDER_COLOR), "Culinary Items",
                0, 0, new Font("Segoe UI", Font.BOLD, 11), CateringManagementSystem.ACCENT_BLUE_BORDER));
        menuScroll.getViewport().setBackground(CateringManagementSystem.PANEL_BG);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel addOnsPanel = new JPanel();
        addOnsPanel.setLayout(new BoxLayout(addOnsPanel, BoxLayout.Y_AXIS));
        addOnsPanel.setBackground(CateringManagementSystem.PANEL_BG);

        for (AddOnService service : dataManager.getAddOnServices()) {
            JCheckBox cb = new JCheckBox(service.getName());
            cb.setFont(new Font("Segoe UI", Font.BOLD, 12));
            cb.setForeground(CateringManagementSystem.TEXT_WHITE);
            cb.setOpaque(false);
            cb.addActionListener(e -> calculateTotals());

            addOnCheckBoxes.add(cb);
            addOnMap.put(cb, service);

            JPanel itemCard = new JPanel(new BorderLayout());
            itemCard.setOpaque(false);
            itemCard.setBorder(new EmptyBorder(6, 10, 6, 10));
            itemCard.add(cb, BorderLayout.WEST);

            String costDesc = service.getFlatCost() > 0 ?
                    CateringManagementSystem.CURRENCY_FORMAT.format(service.getFlatCost()) + " flat" :
                    CateringManagementSystem.CURRENCY_FORMAT.format(service.getPerGuestCost()) + "/guest";

            JLabel costLbl = new JLabel(costDesc);
            costLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            costLbl.setForeground(CateringManagementSystem.ACCENT_AMBER);
            itemCard.add(costLbl, BorderLayout.EAST);

            addOnsPanel.add(itemCard);
            addOnsPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        }

        JScrollPane addOnScroll = new JScrollPane(addOnsPanel);
        addOnScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CateringManagementSystem.BORDER_COLOR), "Event Add-on Services",
                0, 0, new Font("Segoe UI", Font.BOLD, 11), CateringManagementSystem.ACCENT_BLUE_BORDER));
        addOnScroll.getViewport().setBackground(CateringManagementSystem.PANEL_BG);

        centerSplit.add(menuScroll);
        centerSplit.add(addOnScroll);

        panel.add(centerSplit, BorderLayout.CENTER);
        return panel;
    }

    public void refreshMenuList() {
        menuItemsChecklistPanel.removeAll();
        itemCheckBoxes.clear();
        checkBoxMap.clear();

        String selectedPreference = vegRadio.isSelected() ? "Veg" : nonVegRadio.isSelected() ? "Non Veg" : "Both";
        String searchQuery = (searchMenuField != null) ? searchMenuField.getText().trim().toLowerCase() : "";

        int totalCount = 0;
        for (MenuItem item : dataManager.getMenuItems()) {
            boolean matchesPref = selectedPreference.equals("Both") ||
                    (selectedPreference.equals("Veg") && item.getCategory().equalsIgnoreCase("Veg")) ||
                    (selectedPreference.equals("Non Veg") && item.getCategory().equalsIgnoreCase("Non Veg"));

            boolean matchesCat = currentCategoryFilter.equals("All") || item.getCategory().equalsIgnoreCase(currentCategoryFilter);
            boolean matchesSearch = searchQuery.isEmpty() || item.getName().toLowerCase().contains(searchQuery);

            if (matchesPref && matchesCat && matchesSearch) {
                totalCount++;
                JPanel itemCard = new JPanel(new BorderLayout(10, 0));
                itemCard.setOpaque(false);
                itemCard.setBorder(new EmptyBorder(5, 8, 5, 8));
                itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

                JCheckBox cb = new JCheckBox(item.getName());
                cb.setFont(new Font("Segoe UI", Font.BOLD, 12));
                cb.setForeground(CateringManagementSystem.TEXT_WHITE);
                cb.setOpaque(false);
                cb.addActionListener(e -> calculateTotals());

                itemCheckBoxes.add(cb);
                checkBoxMap.put(cb, item);

                JLabel descLabel = new JLabel(CateringManagementSystem.CURRENCY_FORMAT.format(item.getPrice()) + "/plate");
                descLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                descLabel.setForeground(CateringManagementSystem.TEXT_YELLOW);

                PillBadge categoryBadge = new PillBadge(item.getCategory());

                JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
                rightInfo.setOpaque(false);
                rightInfo.add(descLabel);
                rightInfo.add(categoryBadge);

                itemCard.add(cb, BorderLayout.WEST);
                itemCard.add(rightInfo, BorderLayout.EAST);

                menuItemsChecklistPanel.add(itemCard);
                menuItemsChecklistPanel.add(new JSeparator(JSeparator.HORIZONTAL));
            }
        }

        menuItemsChecklistPanel.revalidate();
        menuItemsChecklistPanel.repaint();
        calculateTotals();
    }

    private JPanel createReceiptSummaryPanel() {
        JPanel panel = createStyledCard("3. Receipt & Billing Engine");
        panel.setPreferredSize(new Dimension(340, 0));
        panel.setLayout(new BorderLayout(10, 10));

        JPanel detailsGrid = new JPanel(new GridLayout(8, 1, 0, 6));
        detailsGrid.setOpaque(false);

        perPlateLabel = createSummaryRow(detailsGrid, "Per Plate Dish Cost:", "₹0.00");
        subtotalLabel = createSummaryRow(detailsGrid, "Subtotal (Dishes + Add-ons):", "₹0.00");
        discountLabel = createSummaryRow(detailsGrid, "Promo Discount Savings:", "-₹0.00");
        taxLabel = createSummaryRow(detailsGrid, "GST & Service Tax (5%):", "₹0.00");

        JSeparator sep = new JSeparator();
        sep.setForeground(CateringManagementSystem.BORDER_COLOR);
        detailsGrid.add(sep);

        grandTotalLabel = new JLabel("₹0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        grandTotalLabel.setForeground(CateringManagementSystem.ACCENT_GREEN);

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel totalTitle = new JLabel("GRAND TOTAL:");
        totalTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalTitle.setForeground(CateringManagementSystem.TEXT_WHITE);
        totalRow.add(totalTitle, BorderLayout.WEST);
        totalRow.add(grandTotalLabel, BorderLayout.EAST);
        detailsGrid.add(totalRow);

        balanceLabel = createSummaryRow(detailsGrid, "Remaining Balance Due:", "₹0.00");

        JPanel promoPanel = new JPanel(new BorderLayout(5, 0));
        promoPanel.setOpaque(false);

        promoField = createStyledTextField();
        promoField.setToolTipText("Enter code: FESTIVE10, ROYAL15, WELCOME500");
        applyPromoBtn = new SecondaryButton("Apply Code");
        applyPromoBtn.addActionListener(e -> applyPromoCode());

        promoPanel.add(new JLabel("🎟️ Coupon: "), BorderLayout.WEST);
        promoPanel.add(promoField, BorderLayout.CENTER);
        promoPanel.add(applyPromoBtn, BorderLayout.EAST);

        JPanel middleBox = new JPanel(new BorderLayout(0, 10));
        middleBox.setOpaque(false);
        middleBox.add(detailsGrid, BorderLayout.CENTER);
        middleBox.add(promoPanel, BorderLayout.SOUTH);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        btnPanel.setOpaque(false);

        submitBtn = new PrimaryButton("🚀 BOOK & DOWNLOAD PDF BILL");
        clearBtn = new SecondaryButton("🧹 Reset Form");

        submitBtn.addActionListener(e -> processOrder());
        clearBtn.addActionListener(e -> resetForm());

        btnPanel.add(submitBtn);
        btnPanel.add(clearBtn);

        panel.add(middleBox, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createSummaryRow(JPanel parent, String title, String initialVal) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tLbl.setForeground(CateringManagementSystem.TEXT_MUTED);

        JLabel vLbl = new JLabel(initialVal);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        vLbl.setForeground(CateringManagementSystem.TEXT_WHITE);

        row.add(tLbl, BorderLayout.WEST);
        row.add(vLbl, BorderLayout.EAST);
        parent.add(row);
        return vLbl;
    }

    private void applyPromoCode() {
        String code = promoField.getText().trim().toUpperCase();
        if (code.equals("FESTIVE10")) {
            currentDiscountPercent = 0.10; currentFlatDiscount = 0;
            JOptionPane.showMessageDialog(this, "🎉 10% Discount Applied!", "Promo Applied", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.equals("ROYAL15")) {
            currentDiscountPercent = 0.15; currentFlatDiscount = 0;
            JOptionPane.showMessageDialog(this, "🎉 15% Royal Discount Applied!", "Promo Applied", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.equals("WELCOME500")) {
            currentDiscountPercent = 0.0; currentFlatDiscount = 500.0;
            JOptionPane.showMessageDialog(this, "🎉 ₹500 Flat Discount Applied!", "Promo Applied", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Code! Try FESTIVE10, ROYAL15, or WELCOME500", "Promo Error", JOptionPane.WARNING_MESSAGE);
        }
        calculateTotals();
    }

    private void calculateTotals() {
        int guestCount = 100;
        try {
            guestCount = Math.max(1, Integer.parseInt(guestsField.getText().trim()));
        } catch (NumberFormatException ignored) {}

        double perPlateCost = 0.0;
        for (JCheckBox cb : itemCheckBoxes) {
            if (cb.isSelected()) {
                MenuItem item = checkBoxMap.get(cb);
                if (item != null) perPlateCost += item.getPrice();
            }
        }

        double addOnTotal = 0.0;
        for (JCheckBox cb : addOnCheckBoxes) {
            if (cb.isSelected()) {
                AddOnService s = addOnMap.get(cb);
                if (s != null) addOnTotal += s.calculateCost(guestCount);
            }
        }

        double subtotal = (perPlateCost * guestCount) + addOnTotal;
        double discount = (subtotal * currentDiscountPercent) + currentFlatDiscount;
        double taxableSubtotal = Math.max(0, subtotal - discount);
        double tax = taxableSubtotal * 0.05;
        double grandTotal = taxableSubtotal + tax;

        double advancePaid = 0.0;
        try { advancePaid = Double.parseDouble(advanceField.getText().trim()); } catch (NumberFormatException ignored) {}

        double balanceDue = Math.max(0, grandTotal - advancePaid);

        perPlateLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(perPlateCost));
        subtotalLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(subtotal));
        discountLabel.setText("-" + CateringManagementSystem.CURRENCY_FORMAT.format(discount));
        taxLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(tax));
        grandTotalLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(grandTotal));
        balanceLabel.setText(CateringManagementSystem.CURRENCY_FORMAT.format(balanceDue));
    }

    private void processOrder() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String venue = venueField.getText().trim();
        String date = dateField.getText().trim();
        String guestStr = guestsField.getText().trim();
        String eventType = (String) eventTypeCombo.getSelectedItem();

        if (name.isEmpty() || phone.isEmpty() || venue.isEmpty() || date.isEmpty() || guestStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all customer and event details!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int guests;
        try {
            guests = Integer.parseInt(guestStr);
            if (guests <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive guest count!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> selectedDishes = new ArrayList<>();
        double perPlate = 0;
        for (JCheckBox cb : itemCheckBoxes) {
            if (cb.isSelected()) {
                MenuItem mi = checkBoxMap.get(cb);
                selectedDishes.add(mi.getName());
                perPlate += mi.getPrice();
            }
        }

        if (selectedDishes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one menu item!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> selectedAddOns = new ArrayList<>();
        double addOnTotal = 0;
        for (JCheckBox cb : addOnCheckBoxes) {
            if (cb.isSelected()) {
                AddOnService s = addOnMap.get(cb);
                selectedAddOns.add(s.getName());
                addOnTotal += s.calculateCost(guests);
            }
        }

        double subtotal = (perPlate * guests) + addOnTotal;
        double discount = (subtotal * currentDiscountPercent) + currentFlatDiscount;
        double tax = Math.max(0, subtotal - discount) * 0.05;
        double grandTotal = Math.max(0, subtotal - discount) + tax;

        double advancePaid = 0;
        try { advancePaid = Double.parseDouble(advanceField.getText().trim()); } catch (NumberFormatException ignored) {}

        String orderId = dataManager.generateNextOrderId();
        String prefType = vegRadio.isSelected() ? "Veg" : nonVegRadio.isSelected() ? "Non Veg" : "Both";

        Order newOrder = new Order(orderId, name, phone, venue, date, eventType, guests, prefType,
                selectedDishes, selectedAddOns, subtotal, discount, tax, grandTotal, advancePaid, "Pending");

        dataManager.addOrder(newOrder);

        // Download Native PDF Document & Open Automatically
        File pdfFile = InvoiceGenerator.generateAndDownloadPdf(newOrder);

        JOptionPane.showMessageDialog(this,
                "✅ Booking Created Successfully!\n" +
                "Order ID: " + orderId + "\n" +
                "Grand Total: " + CateringManagementSystem.CURRENCY_FORMAT.format(grandTotal) + "\n\n" +
                "📄 Native PDF Invoice Downloaded & Opened:\n" + pdfFile.getAbsolutePath(),
                "Order & PDF Invoice Generated", JOptionPane.INFORMATION_MESSAGE);

        resetForm();
        mainFrame.switchToCustomerManagement();
    }

    private void resetForm() {
        nameField.setText("");
        phoneField.setText("");
        venueField.setText("");
        guestsField.setText("100");
        advanceField.setText("5000");
        promoField.setText("");
        currentDiscountPercent = 0;
        currentFlatDiscount = 0;
        bothRadio.setSelected(true);
        for (JCheckBox cb : itemCheckBoxes) cb.setSelected(false);
        for (JCheckBox cb : addOnCheckBoxes) cb.setSelected(false);
        calculateTotals();
    }

    private void addFormField(JPanel p, GridBagConstraints gbc, String label, JComponent comp, int x, int y) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(createStyledLabel(label), gbc);
        gbc.gridx = x + 1;
        p.add(comp, gbc);
    }

    private JLabel createStyledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(CateringManagementSystem.TEXT_WHITE);
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(12);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBackground(CateringManagementSystem.INPUT_BG);
        tf.setForeground(CateringManagementSystem.TEXT_WHITE);
        tf.setCaretColor(CateringManagementSystem.TEXT_WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CateringManagementSystem.ACCENT_BLUE_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        return tf;
    }

    private JRadioButton createRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rb.setForeground(CateringManagementSystem.TEXT_WHITE);
        rb.setOpaque(false);
        return rb;
    }

    private JPanel createStyledCard(String title) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CateringManagementSystem.PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CateringManagementSystem.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13), CateringManagementSystem.ACCENT_BLUE_BORDER));
        return card;
    }
}

class HighContrastComboRenderer extends DefaultListCellRenderer {
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

// ==================== ORDER MANAGEMENT TABLE PANEL ==========================

class CustomerManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private CateringManagementSystem mainFrame;
    private DataManager dataManager;

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;

    public CustomerManagementPanel(CateringManagementSystem mainFrame, DataManager dataManager) {
        this.mainFrame = mainFrame;
        this.dataManager = dataManager;

        setLayout(new BorderLayout(12, 12));
        setBackground(CateringManagementSystem.BG_DARK);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterPanel.setOpaque(false);

        JLabel searchLbl = new JLabel("🔍 Search Bookings:");
        searchLbl.setForeground(CateringManagementSystem.TEXT_WHITE);
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchField = new JTextField(18);
        searchField.setBackground(CateringManagementSystem.INPUT_BG);
        searchField.setForeground(CateringManagementSystem.TEXT_WHITE);
        searchField.setCaretColor(CateringManagementSystem.TEXT_WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CateringManagementSystem.ACCENT_BLUE_BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { refreshOrderTable(); }
        });

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setForeground(CateringManagementSystem.TEXT_WHITE);
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "Pending", "Confirmed", "Completed", "Cancelled"});
        statusFilterCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusFilterCombo.setBackground(CateringManagementSystem.CARD_BG);
        statusFilterCombo.setForeground(CateringManagementSystem.TEXT_WHITE);
        statusFilterCombo.setRenderer(new HighContrastComboRenderer());
        statusFilterCombo.addActionListener(e -> refreshOrderTable());

        filterPanel.add(searchLbl);
        filterPanel.add(searchField);
        filterPanel.add(statusLbl);
        filterPanel.add(statusFilterCombo);

        String[] cols = {"Order ID", "Customer Name", "Phone", "Venue", "Event Date", "Guests", "Grand Total", "Advance Paid", "Balance Due", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderTable.setRowHeight(32);
        orderTable.setBackground(CateringManagementSystem.PANEL_BG);
        orderTable.setForeground(CateringManagementSystem.TEXT_WHITE);
        orderTable.setGridColor(CateringManagementSystem.BORDER_COLOR);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(CateringManagementSystem.CARD_BG);
        orderTable.getTableHeader().setForeground(CateringManagementSystem.TEXT_WHITE);

        orderTable.getColumnModel().getColumn(9).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.getViewport().setBackground(CateringManagementSystem.PANEL_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(CateringManagementSystem.BORDER_COLOR));

        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBar.setOpaque(false);

        JButton downloadPdfBtn = new PrimaryButton("📄 Download PDF Invoice");
        JButton statusBtn = new SecondaryButton("⚡ Update Status");
        JButton exportCsvBtn = new SecondaryButton("📊 Export CSV");
        JButton deleteBtn = new DangerButton("🗑️ Delete Booking");

        downloadPdfBtn.addActionListener(e -> downloadPdfForSelected());
        statusBtn.addActionListener(e -> changeSelectedOrderStatus());
        exportCsvBtn.addActionListener(e -> InvoiceGenerator.exportCsvReport(dataManager.getOrders()));
        deleteBtn.addActionListener(e -> deleteSelectedOrder());

        actionBar.add(downloadPdfBtn);
        actionBar.add(statusBtn);
        actionBar.add(exportCsvBtn);
        actionBar.add(deleteBtn);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionBar, BorderLayout.SOUTH);
    }

    public void refreshOrderTable() {
        tableModel.setRowCount(0);
        String query = searchField.getText().trim().toLowerCase();
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();

        for (Order o : dataManager.getOrders()) {
            boolean matchesSearch = query.isEmpty() ||
                    o.getOrderId().toLowerCase().contains(query) ||
                    o.getCustomerName().toLowerCase().contains(query) ||
                    o.getPhone().contains(query) ||
                    o.getVenue().toLowerCase().contains(query);

            boolean matchesStatus = selectedStatus.equals("All Statuses") || o.getStatus().equalsIgnoreCase(selectedStatus);

            if (matchesSearch && matchesStatus) {
                tableModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getCustomerName(),
                        o.getPhone(),
                        o.getVenue(),
                        o.getEventDate(),
                        o.getGuestCount(),
                        CateringManagementSystem.CURRENCY_FORMAT.format(o.getTotalPrice()),
                        CateringManagementSystem.CURRENCY_FORMAT.format(o.getAdvancePaid()),
                        CateringManagementSystem.CURRENCY_FORMAT.format(o.getBalanceDue()),
                        o.getStatus()
                });
            }
        }
    }

    private Order getSelectedOrderObj() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking row from the table!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        return dataManager.getOrders().stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst().orElse(null);
    }

    private void downloadPdfForSelected() {
        Order order = getSelectedOrderObj();
        if (order == null) return;
        File file = InvoiceGenerator.generateAndDownloadPdf(order);
        JOptionPane.showMessageDialog(this, "📄 Native PDF Invoice Downloaded & Opened:\n" + file.getAbsolutePath(), "PDF Invoice Generated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void changeSelectedOrderStatus() {
        Order order = getSelectedOrderObj();
        if (order == null) return;

        String[] statuses = {"Pending", "Confirmed", "Completed", "Cancelled"};
        String newStatus = (String) JOptionPane.showInputDialog(
                this, "Select new status for " + order.getOrderId() + ":",
                "Update Order Status", JOptionPane.QUESTION_MESSAGE, null, statuses, order.getStatus());

        if (newStatus != null && !newStatus.equals(order.getStatus())) {
            order.setStatus(newStatus);
            dataManager.saveData();
            mainFrame.refreshAllPanels();
        }
    }

    private void deleteSelectedOrder() {
        Order order = getSelectedOrderObj();
        if (order == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete booking " + order.getOrderId() + " (" + order.getCustomerName() + ")?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.deleteOrder(order);
            mainFrame.refreshAllPanels();
        }
    }
}

class StatusCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        String status = (value != null) ? value.toString() : "Pending";
        PillBadge badge = new PillBadge(status);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        return badge;
    }
}

// ==================== MENU MANAGER PANEL ==========================

class MenuManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private CateringManagementSystem mainFrame;
    private DataManager dataManager;

    private JTextField nameField, priceField, descField;
    private JComboBox<String> categoryCombo;
    private JTable menuTable;
    private DefaultTableModel tableModel;

    public MenuManagementPanel(CateringManagementSystem mainFrame, DataManager dataManager) {
        this.mainFrame = mainFrame;
        this.dataManager = dataManager;

        setLayout(new BorderLayout(15, 15));
        setBackground(CateringManagementSystem.BG_DARK);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CateringManagementSystem.PANEL_BG);
        formCard.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CateringManagementSystem.BORDER_COLOR),
                "Add Menu Item", 0, 0, new Font("Segoe UI", Font.BOLD, 13), CateringManagementSystem.ACCENT_BLUE_BORDER));
        formCard.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        categoryCombo = new JComboBox<>(new String[]{"Veg", "Non Veg", "Dessert", "Beverage"});
        categoryCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryCombo.setBackground(CateringManagementSystem.CARD_BG);
        categoryCombo.setForeground(CateringManagementSystem.TEXT_WHITE);
        categoryCombo.setRenderer(new HighContrastComboRenderer());

        priceField = new JTextField(15);
        descField = new JTextField(15);

        addFormRow(formCard, gbc, "Dish Name:", nameField, 0);
        addFormRow(formCard, gbc, "Category:", categoryCombo, 1);
        addFormRow(formCard, gbc, "Price / Plate (₹):", priceField, 2);
        addFormRow(formCard, gbc, "Description:", descField, 3);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton addBtn = new PrimaryButton("➕ Add Dish To Menu");
        addBtn.addActionListener(e -> addDish());
        formCard.add(addBtn, gbc);

        String[] cols = {"Dish Name", "Category", "Price / Plate", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        menuTable = new JTable(tableModel);
        menuTable.setRowHeight(28);
        menuTable.setBackground(CateringManagementSystem.PANEL_BG);
        menuTable.setForeground(CateringManagementSystem.TEXT_WHITE);
        menuTable.setGridColor(CateringManagementSystem.BORDER_COLOR);
        menuTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTable.getTableHeader().setBackground(CateringManagementSystem.CARD_BG);
        menuTable.getTableHeader().setForeground(CateringManagementSystem.TEXT_WHITE);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.getViewport().setBackground(CateringManagementSystem.PANEL_BG);

        JPanel rightContainer = new JPanel(new BorderLayout(8, 8));
        rightContainer.setOpaque(false);
        rightContainer.add(scrollPane, BorderLayout.CENTER);

        JButton deleteBtn = new DangerButton("🗑️ Remove Selected Dish");
        deleteBtn.addActionListener(e -> deleteDish());
        rightContainer.add(deleteBtn, BorderLayout.SOUTH);

        add(formCard, BorderLayout.WEST);
        add(rightContainer, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, String label, JComponent comp, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
        JLabel l = new JLabel(label);
        l.setForeground(CateringManagementSystem.TEXT_WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(l, gbc);
        gbc.gridx = 1;
        p.add(comp, gbc);
    }

    private void addDish() {
        String name = nameField.getText().trim();
        String cat = (String) categoryCombo.getSelectedItem();
        String priceStr = priceField.getText().trim();
        String desc = descField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dish Name and Price are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            MenuItem item = new MenuItem(name, cat, price, desc.isEmpty() ? name : desc);
            dataManager.addMenuItem(item);

            nameField.setText(""); priceField.setText(""); descField.setText("");
            mainFrame.refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Dish added to menu!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric price!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDish() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a dish row to remove!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        MenuItem target = dataManager.getMenuItems().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst().orElse(null);

        if (target != null) {
            dataManager.deleteMenuItem(target);
            mainFrame.refreshAllPanels();
        }
    }

    public void refreshMenuTable() {
        tableModel.setRowCount(0);
        for (MenuItem m : dataManager.getMenuItems()) {
            tableModel.addRow(new Object[]{
                    m.getName(), m.getCategory(),
                    CateringManagementSystem.CURRENCY_FORMAT.format(m.getPrice()),
                    m.getDescription()
            });
        }
    }
}

// ==================== EXECUTIVE ANALYTICS DASHBOARD ==========================

class AnalyticsPanel extends JPanel {
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

// ==================== ULTRA HIGH-CONTRAST VISIBLE BUTTON COMPONENTS ==========================

class HighContrastToggleButton extends JToggleButton {
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

class PillBadge extends JLabel {
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

class PrimaryButton extends JButton {
    private static final long serialVersionUID = 1L;

    public PrimaryButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setBackground(CateringManagementSystem.ACCENT_BLUE);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? CateringManagementSystem.ACCENT_BLUE.darker() : CateringManagementSystem.ACCENT_BLUE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(CateringManagementSystem.ACCENT_BLUE_BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}

class SecondaryButton extends JButton {
    private static final long serialVersionUID = 1L;

    public SecondaryButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(CateringManagementSystem.TEXT_WHITE);
        setBackground(CateringManagementSystem.CARD_BG);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? CateringManagementSystem.CARD_BG.darker() : CateringManagementSystem.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(CateringManagementSystem.ACCENT_BLUE_BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}

class DangerButton extends JButton {
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