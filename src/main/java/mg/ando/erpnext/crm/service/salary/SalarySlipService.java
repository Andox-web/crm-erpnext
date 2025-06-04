package mg.ando.erpnext.crm.service.salary;

import java.util.List;

import mg.ando.erpnext.crm.dto.SalarySlipDTO;

public interface SalarySlipService {
    SalarySlipDTO getSalarySlipByName(String name);
    List<SalarySlipDTO> getAllSalarySlips();
    void createSalarySlip(SalarySlipDTO salarySlipDTO);
    int createAllSalarySlips(List<SalarySlipDTO> salarySlipDTOs);
    void deleteSalarySlip(String name);
    int deleteAllSalarySlips(List<String> salarySlipNames);
    List<SalarySlipDTO> getSalarySlipsByEmployee(String employeeName);
}