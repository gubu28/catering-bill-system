package com.mycompany.cateringmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class CustomerManagementPanel extends JPanel {
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

        JButton payBalanceBtn = new PrimaryButton("💳 Pay Full / Settle Balance");
        JButton downloadPdfBtn = new SecondaryButton("💾 Download PDF Invoice");
        JButton statusBtn = new SecondaryButton("⚡ Update Status");
        JButton exportCsvBtn = new SecondaryButton("📊 Export CSV");
        JButton deleteBtn = new DangerButton("🗑️ Delete Booking");

        payBalanceBtn.addActionListener(e -> payFullBalanceForSelected());
        downloadPdfBtn.addActionListener(e -> downloadPdfForSelected());
        statusBtn.addActionListener(e -> changeSelectedOrderStatus());
        exportCsvBtn.addActionListener(e -> InvoiceGenerator.exportCsvReport(dataManager.getOrders()));
        deleteBtn.addActionListener(e -> deleteSelectedOrder());

        actionBar.add(payBalanceBtn);
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

    private void payFullBalanceForSelected() {
        Order order = getSelectedOrderObj();
        if (order == null) return;

        if (order.getBalanceDue() <= 0.01) {
            JOptionPane.showMessageDialog(this, "This order is already FULLY PAID! Balance Due is ₹0.00.", "Payment Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Settle remaining balance for " + order.getOrderId() + " (" + order.getCustomerName() + ")?\n" +
                "Current Balance Due: " + CateringManagementSystem.CURRENCY_FORMAT.format(order.getBalanceDue()) + "\n\n" +
                "Click YES to record full payment, update status to COMPLETED, and regenerate PDF invoice.",
                "Settle Full Payment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            order.setFullyPaid();
            dataManager.saveData();

            // DYNAMICALLY REGENERATE & SYNC UPDATED PDF BILL WITH BALANCE DUE = 0
            File pdfFile = InvoiceGenerator.saveAndDownloadPdf(this, order);

            mainFrame.refreshAllPanels();

            JOptionPane.showMessageDialog(this,
                    "🎉 Payment Succeeded! Order is now FULLY PAID.\n" +
                    "Status: Completed\n" +
                    "Balance Due: ₹0.00\n\n" +
                    "📄 Updated PDF Invoice Generated & Saved:\n" + pdfFile.getAbsolutePath(),
                    "Order Paid & PDF Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void downloadPdfForSelected() {
        Order order = getSelectedOrderObj();
        if (order == null) return;

        // Auto Sync & Download Updated PDF
        File file = InvoiceGenerator.saveAndDownloadPdf(this, order);
        JOptionPane.showMessageDialog(this, "📄 PDF Invoice Downloaded & Opened:\n" + file.getAbsolutePath(), "PDF Invoice Saved", JOptionPane.INFORMATION_MESSAGE);
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

            // AUTO-REGENERATE PDF BILL WITH NEW STATUS & BALANCES
            InvoiceGenerator.updateAndSyncPdfInvoice(order);

            dataManager.saveData();
            mainFrame.refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Status updated to " + newStatus + "! Updated PDF bill regenerated.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
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
