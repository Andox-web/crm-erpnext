package mg.ando.erpnext.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class QuoteItem {
    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("item_name")
    private String itemName;
    private String name;
    private BigDecimal qty;
    private BigDecimal rate;
    private BigDecimal amount;
    public static QuoteItem fromJson(JsonNode jsonNode){
        QuoteItem quoteItem = new QuoteItem();
        quoteItem.setAmount(jsonNode.get("amount").decimalValue());
        quoteItem.setItemCode(jsonNode.get("item_code").asText());
        quoteItem.setItemName(jsonNode.get("iten_name").asText());
        quoteItem.setName(jsonNode.get("name").asText());
        quoteItem.setQty(jsonNode.get("qty").decimalValue());
        quoteItem.setRate(jsonNode.get("rate").decimalValue());
        return quoteItem;
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
    @Override
    public String toString() {
        return "QuoteItem [itemCode=" + itemCode + ", itemName=" + itemName + ", qty=" + qty + ", rate=" + rate
                + ", amount=" + amount + "]";
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
