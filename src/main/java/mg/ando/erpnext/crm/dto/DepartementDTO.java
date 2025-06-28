package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabDepartment`")  // Nom de table sans espace mais avec backticks par convention
public class DepartementDTO {

    @Id
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "docstatus")
    private int docstatus;
    
    @Column(name = "department_name")
    @JsonProperty("department_name")
    private String departmentName;
    
    @Column(name = "parent_department")
    @JsonProperty("parent_department")
    private String parentDepartment;
    
    @Column(name = "is_group")
    @JsonProperty("is_group")
    private boolean isGroup;
    
    @Column(name = "company")
    @JsonProperty("company")
    private String company;
}