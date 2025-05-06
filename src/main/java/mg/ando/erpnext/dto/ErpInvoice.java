package mg.ando.erpnext.dto;

import java.math.BigDecimal;
import java.util.List;

public class ErpInvoice {
    private String name;
    private String posting_date;
    private String due_date;
    private String status;
    private BigDecimal grand_total;
    private String currency;
    private String party_name;
    private String supplier_name;
    private String company;
    private List<ErpInvoiceItem> items;
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getPosting_date() {
        return posting_date;
    }
    public void setPosting_date(String posting_date) {
        this.posting_date = posting_date;
    }
    public String getDue_date() {
        return due_date;
    }
    public void setDue_date(String due_date) {
        this.due_date = due_date;
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
    public String getParty_name() {
        return supplier_name;
    }
    
    public List<ErpInvoiceItem> getItems() {
        return items;
    }
    public void setItems(List<ErpInvoiceItem> items) {
        this.items = items;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }
    
}
