package mg.ando.erpnext.crm.service.employe;

import java.util.List;
import java.util.Map;

import mg.ando.erpnext.crm.dto.EmployeDTO;

public interface EmployeService {
    EmployeDTO getEmployeByName(String name);
    List<EmployeDTO> rechercherEmployes(Map<String, String> filtre);
    List<EmployeDTO> getAllEmployes();
    void createEmploye(EmployeDTO employeDTO);
    void deleteEmploye(String name);
    int createAllEmployes(List<EmployeDTO> employeDTOs);
    int deleteAllEmployes(List<String> employeNames);
}
