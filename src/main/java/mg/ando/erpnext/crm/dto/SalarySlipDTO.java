package mg.ando.erpnext.crm.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabSalary Slip`")  // Échappement du nom de table avec espace
public class SalarySlipDTO {

    @Id
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "docstatus")
    private int docstatus;

    @Column(name = "employee")
    @JsonProperty("employee")
    private String employee;

    @Column(name = "employee_name")
    @JsonProperty("employee_name")
    private String employeeName;

    @Column(name = "start_date")
    @JsonProperty("start_date")
    private String startDate;

    @Column(name = "end_date")
    @JsonProperty("end_date")
    private String endDate;

    @Column(name = "company")
    @JsonProperty("company")
    private String company;

    @Column(name = "currency")
    @JsonProperty("currency")
    private String currency;

    @Column(name = "gross_pay")
    @JsonProperty("gross_pay")
    private Double grossPay;

    @Column(name = "net_pay")
    @JsonProperty("net_pay")
    private Double netPay;

    @Column(name = "status")
    @JsonProperty("status")
    private String status;

    @Column(name = "salary_structure")
    @JsonProperty("salary_structure")
    private String salaryStructure;

    @Column(name = "total_deduction")
    @JsonProperty("total_deduction")
    private Double totalDeduction;
    
    @Column(name = "rounded_total")
    @JsonProperty("rounded_total")
    private Double roundedTotal;

    @Column(name = "total_in_words")
    @JsonProperty("total_in_words")
    private String totalInWords;
    
    // Ces champs ne sont pas stockés dans la table principale
    @Transient
    private List<SalaryDetailDTO> earnings = new ArrayList<>();
    
    @Transient
    private List<SalaryDetailDTO> deductions = new ArrayList<>();
}