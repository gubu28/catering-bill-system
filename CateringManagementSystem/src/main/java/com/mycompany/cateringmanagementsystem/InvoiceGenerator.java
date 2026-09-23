package com.mycompany.cateringmanagementsystem;

import java.awt.Component;
import java.awt.Desktop;
import java.io.*;
import java.util.List;
import javax.swing.JFileChooser;

public class InvoiceGenerator {

    /**
     * Regenerates & updates the PDF & HTML invoice files for an order, displaying updated Balance Due (₹0.00) & Status.
     */
    public static File updateAndSyncPdfInvoice(Order order) {
        File dir = new File("invoices");
        if (!dir.exists()) dir.mkdirs();

        File pdfFile = new File(dir, "Invoice_" + order.getOrderId() + ".pdf");
        File htmlFile = new File(dir, "Invoice_" + order.getOrderId() + ".html");

        generateHtmlInvoice(order, htmlFile);
        generatePdfFile(order, pdfFile);

        return pdfFile;
    }

    public static File saveAndDownloadPdf(Component parent, Order order) {
        File pdfFile = updateAndSyncPdfInvoice(order);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("💾 Save PDF Invoice As...");
        chooser.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop/Invoice_" + order.getOrderId() + ".pdf"));

        int userSelection = chooser.showSaveDialog(parent);
        File finalSavedFile = pdfFile;

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File chosenFile = chooser.getSelectedFile();
            if (!chosenFile.getName().endsWith(".pdf")) {
                chosenFile = new File(chosenFile.getAbsolutePath() + ".pdf");
            }
            copyFile(pdfFile, chosenFile);
            finalSavedFile = chosenFile;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(finalSavedFile);
            } else {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + finalSavedFile.getAbsolutePath() + "\"");
            }
        } catch (Exception ignored) {}

        return finalSavedFile;
    }

    private static void copyFile(File src, File dest) {
        try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            System.err.println("Copy failed: " + e.getMessage());
        }
    }

    private static void generatePdfFile(Order order, File pdfFile) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pdfFile))) {
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(content);

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
            writer.println("0 -16 Td");
            writer.println("(ORDER STATUS: " + cleanPdfText(order.getStatus()).toUpperCase() + "  |  PAYMENT: " + cleanPdfText(order.getPaymentStatus()).toUpperCase() + ") Tj");
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
            writer.println("(Total Paid: Rs. " + String.format("%.2f", order.getAdvancePaid()) + "   |   Balance Due: Rs. " + String.format("%.2f", order.getBalanceDue()) + ") Tj");

            if (order.getBalanceDue() <= 0.01) {
                writer.println("0 -22 Td");
                writer.println("/F1 14 Tf");
                writer.println("(*** PAID IN FULL - BALANCE DUE: Rs 0.00 ***) Tj");
            }

            writer.println("0 -30 Td");
            writer.println("/F2 10 Tf");
            writer.println("(Thank you for choosing Executive Catering Services!) Tj");
            writer.println("ET");
            writer.flush();

            byte[] streamData = content.toByteArray();

            PrintWriter pdfPW = new PrintWriter(bos);
            pdfPW.println("%PDF-1.4");

            pdfPW.println("1 0 obj");
            pdfPW.println("<< /Type /Catalog /Pages 2 0 R >>");
            pdfPW.println("endobj");

            pdfPW.println("2 0 obj");
            pdfPW.println("<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
            pdfPW.println("endobj");

            pdfPW.println("3 0 obj");
            pdfPW.println("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R /F2 5 0 R >> >> /Contents 6 0 R >>");
            pdfPW.println("endobj");

            pdfPW.println("4 0 obj");
            pdfPW.println("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>");
            pdfPW.println("endobj");

            pdfPW.println("5 0 obj");
            pdfPW.println("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
            pdfPW.println("endobj");

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
            System.err.println("PDF Exception: " + e.getMessage());
        }
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
        html.append(".paid-badge { background: #16a34a; color: white; padding: 8px 16px; border-radius: 6px; font-size: 14px; font-weight: bold; display: inline-block; margin-top: 10px; }");
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
        html.append("<div><div class='label'>BOOKING STATUS</div><div class='val'>").append(order.getStatus()).append(" (").append(order.getPaymentStatus()).append(")</div></div>");
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
        html.append("<div style='margin-top:10px;'>Total Paid: <strong style='color:#60a5fa;'>").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getAdvancePaid())).append("</strong> | Balance Due: <strong style='color:#f87171;'>").append(CateringManagementSystem.CURRENCY_FORMAT.format(order.getBalanceDue())).append("</strong></div>");

        if (order.getBalanceDue() <= 0.01) {
            html.append("<div class='paid-badge'>✅ FULLY PAID - BALANCE DUE: ₹0.00</div>");
        }

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
