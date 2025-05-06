package mg.ando.erpnext.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class SupplierQuote {
    private String name;
    private LocalDate transactionDate;
    private LocalDate validTill;
    private String status;
    private BigDecimal grand_total;
    private String currency;
    private String supplier;
    
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
    public LocalDate getValidTill() {
        return validTill;
    }
    public void setValidTill(LocalDate validTill) {
        this.validTill = validTill;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public static SupplierQuote fromJson(JsonNode erpQuote) {
        SupplierQuote quote = new SupplierQuote();
        
        // Conversion des champs de base
        quote.setName(erpQuote.get("name").asText());
        quote.setStatus(erpQuote.get("status").asText());
        quote.setCurrency(erpQuote.get("currency").asText());
        quote.setGrand_total(new BigDecimal(erpQuote.get("grand_total").asDouble()));
        quote.setSupplier(erpQuote.get("supplier").asText());
        // Conversion des dates
        try {
            if (erpQuote.get("transaction_date") != null) {
                quote.setTransactionDate(
                    LocalDate.parse(erpQuote.get("transaction_date").asText(), 
                    DateTimeFormatter.ISO_LOCAL_DATE)
                );
            }
            
            if (erpQuote.get("valid_till") != null) {
                quote.setValidTill(
                    LocalDate.parse(erpQuote.get("valid_till").asText(), 
                    DateTimeFormatter.ISO_LOCAL_DATE)
                );
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            System.err.println("Erreur de parsing de date pour le devis {}"+ erpQuote.get("name").asText());
        }
        return quote;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSupplier() {
        return supplier;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    public BigDecimal getGrand_total() {
        return grand_total;
    }
    public void setGrand_total(BigDecimal grand_total) {
        this.grand_total = grand_total;
    }   
}

