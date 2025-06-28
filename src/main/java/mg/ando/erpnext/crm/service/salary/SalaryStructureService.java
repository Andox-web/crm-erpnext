package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import java.util.List;

public interface SalaryStructureService {
    SalaryStructureDTO getSalaryStructureByName(String name);
    SalaryStructureDTO getByName(String name);
    List<SalaryStructureDTO> getAllSalaryStructures();
    SalaryStructureDTO createSalaryStructure(SalaryStructureDTO salaryStructureDTO);
    List<SalaryStructureDTO> createAllSalaryStructures(List<SalaryStructureDTO> structures);
    void deleteSalaryStructure(String name);
    int deleteAllSalaryStructures(List<String> names);
    List<SalaryStructureDTO> getWithFilters(List<Filter> filters);
}