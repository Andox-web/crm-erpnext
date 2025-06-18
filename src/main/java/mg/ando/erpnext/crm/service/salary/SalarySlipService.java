package mg.ando.erpnext.crm.service.salary;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.dto.MonthSalaryDetail;
import mg.ando.erpnext.crm.dto.MonthSalarySlip;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;

public interface SalarySlipService {
    SalarySlipDTO getSalarySlipByName(String name);
    List<SalarySlipDTO> getAllSalarySlips();
    SalarySlipDTO createSalarySlip(SalarySlipDTO salarySlipDTO);
    List<SalarySlipDTO> createAllSalarySlips(List<SalarySlipDTO> salarySlipDTOs);
    void deleteSalarySlip(String name);
    int deleteAllSalarySlips(List<String> salarySlipNames);
    List<SalarySlipDTO> getSalarySlipsByEmployee(String employeeName);
    List<SalarySlipDTO> findSalarySlipsByMonth(Map<String, String> filtre) ;
    List<SalarySlipDTO> getWithFilters(List<Filter> filters);

    boolean cancel(SalarySlipDTO salarySlipDTO);
    boolean submit(SalarySlipDTO salarySlipDTO);
    Map<YearMonth,MonthSalarySlip> groupSalarySlipByMonthForYear(String year);
    Map<String,Map<YearMonth,MonthSalaryDetail>> groupSalaryDetailByMonthForYear(String year);
    List<MonthSalaryDetail> groupSalaryDetailByMonth(YearMonth yearMonth);
    MonthSalarySlip groupSalarySlipByMonth(YearMonth yearMonth);
    void insertSalarySlipForEmployeInPeriod(String employeeName,LocalDate dateDebut,LocalDate dateFin,String SalaireBase);
}