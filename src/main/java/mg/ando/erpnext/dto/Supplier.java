package mg.ando.erpnext.dto;

import com.fasterxml.jackson.databind.JsonNode;

import mg.ando.erpnext.util.Util;

public class Supplier {
    private String name;
    private String supplierName;
    private String supplierType;
    private String emailId;
    private String mobileNo;
    private String modified;

    // Getters and Setters
    
    public static Supplier convertFromJson(JsonNode node) {
        Supplier supplier = new Supplier();
        supplier.setName(Util.getTextValue(node, "name"));
        supplier.setSupplierName(Util.getTextValue(node, "supplier_name"));
        supplier.setSupplierType(Util.getTextValue(node, "supplier_type"));
        supplier.setEmailId(Util.getTextValue(node, "email_id"));
        supplier.setMobileNo(Util.getTextValue(node, "mobile_no"));
        supplier.setModified(Util.getTextValue(node, "modified"));
        return supplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
    
}