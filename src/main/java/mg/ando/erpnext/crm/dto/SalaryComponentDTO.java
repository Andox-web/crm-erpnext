package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalaryComponentDTO {
    @JsonProperty("salary_component")
    private String salaryComponent;

    private String abbr;
    
    private String type;

    private String formula;

    @JsonProperty("amount_based_on_formula")
    private boolean amountBasedOnFormula;
}