package mg.ando.erpnext.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "`tabFiscal Year`") 
public class FiscalYearDTO {

    @JsonProperty("name")
    @Column(name = "name")
    @Id
    private String name;

    @Column(name = "year_start_date")
    @JsonProperty("year_start_date")
    private String yearStartDate;

    @Column(name = "year_end_date")
    @JsonProperty("year_end_date")
    private String yearEndDate;

    @Column(name = "is_short_year")
    @JsonProperty("is_short_year")
    private boolean isShortYear;

}