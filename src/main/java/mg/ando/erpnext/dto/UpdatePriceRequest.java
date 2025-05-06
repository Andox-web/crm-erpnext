package mg.ando.erpnext.dto;

import java.math.BigDecimal;

public class UpdatePriceRequest {
    private String name; // Nom de la ligne dans ERPNext (ex: "a02b4d9a2e")
    private double rate;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getRate() {
        return rate;
    }
    public void setRate(double rate) {
        this.rate = rate;
    }
    
    
}

