package mg.ando.erpnext.crm.dto;

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
@Table(name = "`tabSalary Structure`") 
public class SalaryStructureDTO {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "docstatus")
    private int docstatus;
    
    @Column(name = "company")
    private String company;

    @Column(name = "is_active")
    @JsonProperty("is_active")
    private String isActive;

    @Column(name = "payroll_frequency")
    @JsonProperty("payroll_frequency")
    private String payrollFrequency;
    
    @Transient
    private List<SalaryDetailDTO> earnings;
    
    @Transient
    private List<SalaryDetailDTO> deductions;
}