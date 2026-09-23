package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OrderManagementPanel extends JPanel {
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

    private Set<String> selectedDishNames;

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

        selectedDishNames = new HashSet<>();
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
        JPanel panel = createStyledCard("2. Select Menu Items (Persistent Selection) & Add-ons");
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
        selectedCounterLabel = new JLabel("Selected: 0 Dishes");
        selectedCounterLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        selectedCounterLabel.setForeground(CateringManagementSystem.TEXT_YELLOW);

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

        for (MenuItem item : dataManager.getMenuItems()) {
            boolean matchesPref = selectedPreference.equals("Both") ||
                    (selectedPreference.equals("Veg") && item.getCategory().equalsIgnoreCase("Veg")) ||
                    (selectedPreference.equals("Non Veg") && item.getCategory().equalsIgnoreCase("Non Veg"));

            boolean matchesCat = currentCategoryFilter.equals("All") || item.getCategory().equalsIgnoreCase(currentCategoryFilter);
            boolean matchesSearch = searchQuery.isEmpty() || item.getName().toLowerCase().contains(searchQuery);

            if (matchesPref && matchesCat && matchesSearch) {
                JPanel itemCard = new JPanel(new BorderLayout(10, 0));
                itemCard.setOpaque(false);
                itemCard.setBorder(new EmptyBorder(5, 8, 5, 8));
                itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

                JCheckBox cb = new JCheckBox(item.getName());
                cb.setFont(new Font("Segoe UI", Font.BOLD, 12));
                cb.setForeground(CateringManagementSystem.TEXT_WHITE);
                cb.setOpaque(false);

                if (selectedDishNames.contains(item.getName())) {
                    cb.setSelected(true);
                }

                cb.addActionListener(e -> {
                    if (cb.isSelected()) {
                        selectedDishNames.add(item.getName());
                    } else {
                        selectedDishNames.remove(item.getName());
                    }
                    calculateTotals();
                });

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

        submitBtn = new PrimaryButton("💾 BOOK & SAVE PDF INVOICE");
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

        Map<String, MenuItem> allMenuMap = dataManager.getMenuItems().stream()
                .collect(Collectors.toMap(MenuItem::getName, m -> m, (a, b) -> a));

        for (String dishName : selectedDishNames) {
            MenuItem item = allMenuMap.get(dishName);
            if (item != null) perPlateCost += item.getPrice();
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

        if (selectedDishNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one menu item!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> selectedDishes = new ArrayList<>(selectedDishNames);
        Map<String, MenuItem> allMenuMap = dataManager.getMenuItems().stream()
                .collect(Collectors.toMap(MenuItem::getName, m -> m, (a, b) -> a));

        double perPlate = 0;
        for (String dishName : selectedDishes) {
            MenuItem mi = allMenuMap.get(dishName);
            if (mi != null) perPlate += mi.getPrice();
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

        File pdfFile = InvoiceGenerator.saveAndDownloadPdf(this, newOrder);

        JOptionPane.showMessageDialog(this,
                "✅ Booking Created & Saved!\n" +
                "Order ID: " + orderId + "\n" +
                "Grand Total: " + CateringManagementSystem.CURRENCY_FORMAT.format(grandTotal) + "\n\n" +
                "📄 PDF Document Saved To:\n" + pdfFile.getAbsolutePath(),
                "PDF Invoice Saved", JOptionPane.INFORMATION_MESSAGE);

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
        selectedDishNames.clear();
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
