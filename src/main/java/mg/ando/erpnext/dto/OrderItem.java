package mg.ando.erpnext.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class OrderItem {
    @JsonProperty("item_code")
    private String itemCode;
    @JsonProperty("item_name")
    private String itemName;
    private BigDecimal qty;
    private BigDecimal rate;
    private BigDecimal amount;
    private String name; // Nom de la ligne dans ERPNext (ex: "a02b4d9a2e")
    public static OrderItem fromJson(JsonNode node) {
        OrderItem item = new OrderItem();
        item.setName(node.get("name").asText(null));
        item.setItemCode(node.get("item_code").asText("N/A"));
        item.setItemName(node.get("item_name").asText(""));
        item.setQty(node.get("qty").decimalValue());
        item.setRate(node.get("rate").decimalValue());
        item.setAmount(node.get("amount").decimalValue());
        return item;
    }
    
    public String getItemCode() {
        return itemCode;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public BigDecimal getQty() {
        return qty;
    }
    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }
    public BigDecimal getRate() {
        return rate;
    }
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}