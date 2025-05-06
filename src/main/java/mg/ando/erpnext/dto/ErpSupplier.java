package mg.ando.erpnext.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErpSupplier {
    private String name;
    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("mobile_no")
    private String mobileNo;

    private String modified;

    @JsonProperty("supplier_type")
    private String supplierType;
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

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }
    
}
