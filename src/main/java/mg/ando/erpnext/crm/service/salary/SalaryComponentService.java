package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryComponentDTO;

import java.util.List;

public interface SalaryComponentService {
    SalaryComponentDTO getSalaryComponentByName(String name);
    List<SalaryComponentDTO> getAllSalaryComponents();
    void createSalaryComponent(SalaryComponentDTO salaryComponentDTO);
    int createAllSalaryComponents(List<SalaryComponentDTO> components);
    void deleteSalaryComponent(String name);
    int deleteAllSalaryComponents(List<String> names);
    List<SalaryComponentDTO> getWithFilters(List<Filter> filters);
}