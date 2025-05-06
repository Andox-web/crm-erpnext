package mg.ando.erpnext.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class RfqItem {
    private String itemCode;
    private String description;
    private double quantity;
    private String uom;

    // Getters/Setters

    public static RfqItem fromJson(JsonNode node) {
        RfqItem item = new RfqItem();
        item.setItemCode(safeText(node, "item_code", "N/A"));
        item.setDescription(safeText(node, "description", ""));
        item.setQuantity(safeDouble(node, "qty"));
        item.setUom(safeText(node, "uom", "EA"));
        return item;
    }

    private static String safeText(JsonNode node, String field, String defaultValue) {
        return node.has(field) ? node.get(field).asText(defaultValue) : defaultValue;
    }

    private static double safeDouble(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asDouble() : 0.0;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
    
}