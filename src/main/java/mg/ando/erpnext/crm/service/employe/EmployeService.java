package mg.ando.erpnext.crm.service.employe;

import java.util.List;
import java.util.Map;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.EmployeDTO;

public interface EmployeService {
    EmployeDTO getEmployeByName(String name);
    EmployeDTO getByName(String name);
    List<EmployeDTO> rechercherEmployes(Map<String, String> filtre);
    List<EmployeDTO> getAllEmployes();
    EmployeDTO createEmploye(EmployeDTO employeDTO);
    void deleteEmploye(String name);
    List<EmployeDTO> createAllEmployes(List<EmployeDTO> employeDTOs);
    int deleteAllEmployes(List<String> employeNames);
    List<EmployeDTO> getWithFilters(List<Filter> filters);
}
