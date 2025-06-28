package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryComponentDTO;

import java.util.List;

public interface SalaryComponentService {
    SalaryComponentDTO getSalaryComponentByName(String name);
    SalaryComponentDTO getByName(String name);
    List<SalaryComponentDTO> getAllSalaryComponents();
    SalaryComponentDTO createSalaryComponent(SalaryComponentDTO salaryComponentDTO);
    List<SalaryComponentDTO> createAllSalaryComponents(List<SalaryComponentDTO> components);
    void deleteSalaryComponent(String name);
    int deleteAllSalaryComponents(List<String> names);
    List<SalaryComponentDTO> getWithFilters(List<Filter> filters);
}