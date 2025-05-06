package mg.ando.erpnext.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

public class SupplierOrder {

    private String name;
    private LocalDate transactionDate;
    private String status;
    private BigDecimal grandTotal;
    private String currency;

    public static SupplierOrder fromJson(JsonNode erpOrder) {
        SupplierOrder order = new SupplierOrder();
        order.setName(erpOrder.get("name").asText());
        order.setTransactionDate(LocalDate.parse(erpOrder.get("transaction_date").asText()));
        order.setStatus(erpOrder.get("status").asText());
        order.setGrandTotal(erpOrder.get("grand_total").decimalValue());
        order.setCurrency(erpOrder.get("currency").asText());
        return order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
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
        
}
