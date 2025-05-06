package mg.ando.erpnext.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class Rfq {
    private String name;
    private String transactionDate;
    private String status;
    private String company;

    // Getters/Setters

    public static Rfq fromJson(JsonNode node) {
        Rfq rfq = new Rfq();
        rfq.setName(getText(node, "name"));
        rfq.setTransactionDate(getText(node, "transaction_date"));
        rfq.setStatus(getText(node, "status"));
        rfq.setCompany(getText(node, "company"));
        return rfq;
    }

    private static String getText(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    
}