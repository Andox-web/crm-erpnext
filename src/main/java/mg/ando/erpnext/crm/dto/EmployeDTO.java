package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmployeDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("employee_name")
    private String employeeName;
    
    @JsonProperty("date_of_joining")
    private String dateOfJoining;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("employee_number")
    private String employeeNumber;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("gender")
    private String gender;
}