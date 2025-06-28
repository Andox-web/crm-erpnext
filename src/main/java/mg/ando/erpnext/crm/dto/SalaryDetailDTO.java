package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabSalary Detail`")  // Échappement du nom de table avec espace
public class SalaryDetailDTO {

    @Id
    @Column(name = "name")
    private String name;
    
    @Column(name = "docstatus")
    private int docstatus;

    @Column(name = "salary_component")
    @JsonProperty("salary_component")
    private String salaryComponent;

    
    @Column(name = "amount_based_on_formula")
    @JsonProperty("amount_based_on_formula")
    private boolean amountBasedOnFormula;

    @Column(name = "formula")
    @JsonProperty("formula")
    private String formula;
    
    @Column(name = "amount")
    @JsonProperty("amount")
    private Double amount;

    @Column(name = "parentfield", insertable = false, updatable = false)
    private String type;

    @Column(name = "parent",insertable = false, updatable = false)
    private String salarySlip;

    @Column(name = "parentfield")
    @JsonProperty("parentfield")
    private String parentfield;
    
    @Column(name = "parent")
    @JsonProperty("parent")
    private String parent;
}