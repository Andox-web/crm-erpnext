package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabEmployee`")
public class EmployeDTO {

    @Id
    @Column(name = "name")
    @JsonProperty("name")
    private String name;

    @Column(name = "docstatus")
    private int docstatus;

    @Column(name = "employee_name")
    @JsonProperty("employee_name")
    private String employeeName;
    
    @Column(name = "date_of_joining")
    @JsonProperty("date_of_joining")
    private String dateOfJoining;

    @Column(name = "date_of_birth")
    @JsonProperty("date_of_birth")
    private String dateOfBirth;
    
    @Column(name = "status")
    @JsonProperty("status")
    private String status;
    
    @Column(name = "company")
    @JsonProperty("company")
    private String company;
    
    @Column(name = "employee_number")
    @JsonProperty("employee_number")
    private String employeeNumber;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "gender")
    @JsonProperty("gender")
    private String gender;

    @Column(name = "department")
    @JsonProperty("department")
    private String department;
}