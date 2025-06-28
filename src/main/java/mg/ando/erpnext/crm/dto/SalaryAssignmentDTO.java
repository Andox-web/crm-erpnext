package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabSalary Structure Assignment`")  // Notez les backticks pour le nom avec espaces
public class SalaryAssignmentDTO {

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

    @Column(name = "company")
    @JsonProperty("company")
    private String company;

    @Column(name = "salary_structure")
    @JsonProperty("salary_structure")
    private String salaryStructure;

    @Column(name = "base")
    @JsonProperty("base")
    private Double base;

    @Column(name = "variable")
    @JsonProperty("variable")
    private Double variable;

    @Column(name = "from_date")
    @JsonProperty("from_date")
    private String fromDate;

    @Transient
    @JsonProperty("to_date")
    private String toDate;

    @Column(name = "currency")
    @JsonProperty("currency")
    private String currency;

}