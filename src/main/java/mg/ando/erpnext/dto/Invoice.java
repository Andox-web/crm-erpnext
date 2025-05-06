package mg.ando.erpnext.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {
    private String name;
    private LocalDate postingDate;
    private LocalDate dueDate;
    private String status;
    private BigDecimal grandTotal;
    private String currency;
    private String supplier;
    private String supplierName;
    private BigDecimal outstandingAmount;
    private String company;

    public static Invoice fromJson(JsonNode node) {
        Invoice invoice = new Invoice();
        invoice.setName(getText(node, "name"));
        invoice.setPostingDate(parseDate(node, "posting_date"));
        invoice.setDueDate(parseDate(node, "due_date"));
        invoice.setStatus(getText(node, "status"));
        invoice.setGrandTotal(getBigDecimal(node, "grand_total"));
        invoice.setCurrency(getText(node, "currency"));
        invoice.setSupplier(getText(node, "supplier"));
        invoice.setSupplierName(getText(node, "supplier_name"));
        invoice.setOutstandingAmount(node.get("outstanding_amount").decimalValue());
        invoice.setCompany(getText(node, "company"));
        return invoice;
    }

    // Getters/Setters

    private static String getText(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "";
    }

    private static BigDecimal getBigDecimal(JsonNode node, String field) {
        return node.has(field) ? new BigDecimal(node.get(field).asText()) : BigDecimal.ZERO;
    }

    private static LocalDate parseDate(JsonNode node, String field) {
        return node.has(field) ? LocalDate.parse(node.get(field).asText()) : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDate postingDate) {
        this.postingDate = postingDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }
    
}