package mg.ando.erpnext.crm.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tabSeries")
public class SeriesDTO {
    
    @Id
    @Column(name = "name")
    private String name;
    
    @Column(name = "current")
    private int current;
}