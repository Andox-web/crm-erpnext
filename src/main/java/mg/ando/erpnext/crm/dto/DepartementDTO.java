package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DepartementDTO {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("department_name")
    private String departmentName;
    
    @JsonProperty("parent_department")
    private String parentDepartment;
    
    @JsonProperty("is_group")
    private boolean isGroup;
    
    @JsonProperty("company")
    private String company;
}