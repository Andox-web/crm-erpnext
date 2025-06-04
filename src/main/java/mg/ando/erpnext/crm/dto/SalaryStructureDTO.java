package mg.ando.erpnext.crm.dto;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class SalaryStructureDTO {
    private String name;
    private String company;

    @JsonProperty("is_active")
    private String isActive;

    @JsonProperty("payroll_frequency")
    private String payrollFrequency;
    
    private List<SalaryDetailDTO> earnings;
    private List<SalaryDetailDTO> deductions;
}