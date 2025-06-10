package mg.ando.erpnext.crm.service.salary;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import mg.ando.erpnext.crm.dto.MonthSalaryDetail;
import mg.ando.erpnext.crm.dto.MonthSalarySlip;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;

public interface SalarySlipService {
    SalarySlipDTO getSalarySlipByName(String name);
    List<SalarySlipDTO> getAllSalarySlips();
    void createSalarySlip(SalarySlipDTO salarySlipDTO);
    int createAllSalarySlips(List<SalarySlipDTO> salarySlipDTOs);
    void deleteSalarySlip(String name);
    int deleteAllSalarySlips(List<String> salarySlipNames);
    List<SalarySlipDTO> getSalarySlipsByEmployee(String employeeName);
    List<SalarySlipDTO> findSalarySlipsByMonth(Map<String, String> filtre) ;

    Map<YearMonth,MonthSalarySlip> groupSalarySlipByMonthForYear(String year);
    Map<String,Map<YearMonth,MonthSalaryDetail>> groupSalaryDetailByMonthForYear(String year);
    List<MonthSalaryDetail> groupSalaryDetailByMonth(YearMonth yearMonth);
    MonthSalarySlip groupSalarySlipByMonth(YearMonth yearMonth);
}