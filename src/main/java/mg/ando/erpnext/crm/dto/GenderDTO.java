package mg.ando.erpnext.crm.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabGender`")  // Supposant que cette table existe
public class GenderDTO {
    
    @Id
    @Column(name = "name")
    private String name;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "docstatus")
    private int docstatus;

}