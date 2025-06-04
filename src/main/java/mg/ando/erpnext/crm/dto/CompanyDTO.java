package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyDTO {

    // Champs principaux
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("company_name")
    private String companyName;
    
    @JsonProperty("default_currency")
    private String defaultCurrency;
}