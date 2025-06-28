package mg.ando.erpnext.crm.dto;

import java.time.YearMonth;

import lombok.Data;

@Data
public class MonthSalaryDetail {
    
    private YearMonth yearMonth;
    private String salaryComponent;

    private Double amount;

    private String type;

    private YearMonth month;
}
