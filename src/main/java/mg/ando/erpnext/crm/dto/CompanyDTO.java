package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabCompany`")
public class CompanyDTO {

    @Id
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "docstatus")
    private int docstatus;
    
    @Column(name = "company_name")
    @JsonProperty("company_name")
    private String companyName;
    
    @Column(name = "default_currency")
    @JsonProperty("default_currency")
    private String defaultCurrency;
}