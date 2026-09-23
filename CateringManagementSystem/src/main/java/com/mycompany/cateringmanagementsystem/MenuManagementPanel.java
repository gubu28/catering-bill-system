package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MenuManagementPanel extends JPanel {
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
