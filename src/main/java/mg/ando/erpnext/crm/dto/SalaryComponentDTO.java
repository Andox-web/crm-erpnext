package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabSalary Component`")  // Échappement du nom de table avec espace
public class SalaryComponentDTO {

    @Id
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "salary_component")
    @JsonProperty("salary_component")
    private String salaryComponent;

    @Column(name = "docstatus")
    private int docstatus;

    @Column(name = "salary_component_abbr")
    @JsonProperty("salary_component_abbr")
    private String abbr;
    
    @Column(name = "type")
    private String type;

    @Column(name = "formula")
    private String formula;

    @Column(name = "amount_based_on_formula")
    @JsonProperty("amount_based_on_formula")
    private boolean amountBasedOnFormula;
}