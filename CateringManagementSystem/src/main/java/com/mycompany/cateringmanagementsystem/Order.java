package com.mycompany.cateringmanagementsystem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
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

        updatePaymentStatus();

        this.status = status;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    public void updatePayment(double additionalAmount) {
        this.advancePaid += additionalAmount;
        if (this.advancePaid > this.totalPrice) this.advancePaid = this.totalPrice;
        this.balanceDue = Math.max(0, totalPrice - advancePaid);
        updatePaymentStatus();
    }

    public void setFullyPaid() {
        this.advancePaid = this.totalPrice;
        this.balanceDue = 0.0;
        this.paymentStatus = "Fully Paid";
        this.status = "Completed";
    }

    private void updatePaymentStatus() {
        if (this.balanceDue <= 0.01) {
            this.balanceDue = 0.0;
            this.paymentStatus = "Fully Paid";
        } else if (this.advancePaid > 0) {
            this.paymentStatus = "Deposit Paid";
        } else {
            this.paymentStatus = "Unpaid";
        }
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
    public void setStatus(String s) {
        this.status = s;
        if (s.equalsIgnoreCase("Completed")) {
            setFullyPaid();
        }
    }
    public String getTimestamp() { return timestamp; }
}
