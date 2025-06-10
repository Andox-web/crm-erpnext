package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalaryDetailDTO {
    @JsonProperty("salary_component")
    private String salaryComponent;
    
    @JsonProperty("amount_based_on_formula")
    private boolean amountBasedOnFormula;

    @JsonProperty("formula")
    private String formula;
    
    @JsonProperty("amount")
    private Double amount;

    private String type;
    private String salarySlip;

    @JsonProperty("parentfield")
    private String parentfield;
    
    @JsonProperty("parent")
    private String parent;
}