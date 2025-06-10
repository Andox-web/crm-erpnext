package mg.ando.erpnext.crm.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalarySlipDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("company")
    private String company;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("gross_pay")
    private Double grossPay;

    @JsonProperty("net_pay")
    private Double netPay;

    @JsonProperty("status")
    private String status;

    @JsonProperty("salary_structure")
    private String salaryStructure;

    @JsonProperty("total_deduction")
    private Double totalDeduction;
    
    @JsonProperty("rounded_total")
    private Double roundedTotal;

    @JsonProperty("total_in_words")
    private String totalInWords;
    
    private List<SalaryDetailDTO> earnings = new ArrayList<>();
    private List<SalaryDetailDTO> deductions = new ArrayList<>();;
}
