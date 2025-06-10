package mg.ando.erpnext.crm.dto;

import java.time.YearMonth;

import lombok.Data;

@Data
public class MonthSalarySlip {

    private YearMonth yearMonth;

    private String startDate;

    private String endDate;

    private Double grossPay;

    private Double netPay;

    private Double totalDeduction;
    
    private Double roundedTotal;
}
