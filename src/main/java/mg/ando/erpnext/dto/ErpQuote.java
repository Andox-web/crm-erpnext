package mg.ando.erpnext.dto;

import java.math.BigDecimal;

public class ErpQuote {
    
    private String name;
    private String transaction_date;
    private String valid_till;
    private String status;
    private BigDecimal grand_total;
    private String currency;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTransaction_date() {
        return transaction_date;
    }
    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }
    public String getValid_till() {
        return valid_till;
    }
    public void setValid_till(String valid_till) {
        this.valid_till = valid_till;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public BigDecimal getGrand_total() {
        return grand_total;
    }
    public void setGrand_total(BigDecimal grand_total) {
        this.grand_total = grand_total;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
