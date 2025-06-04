package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalaryAssignmentDTO {

    // Champs principaux
    @JsonProperty("name")
    private String name;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("company")
    private String company;

    @JsonProperty("salary_structure")
    private String salaryStructure;

    @JsonProperty("base")
    private Double base;

    @JsonProperty("variable")
    private Double variable;

    @JsonProperty("from_date")
    private String fromDate;

    @JsonProperty("to_date")
    private String toDate;

    // Devise et détails
    @JsonProperty("currency")
    private String currency;

    @JsonProperty("payroll_frequency")
    private String payrollFrequency;

    @JsonProperty("taxable_income")
    private Double taxableIncome;
}