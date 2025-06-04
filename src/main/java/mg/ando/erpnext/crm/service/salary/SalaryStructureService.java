package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import java.util.List;

public interface SalaryStructureService {
    SalaryStructureDTO getSalaryStructureByName(String name);
    List<SalaryStructureDTO> getAllSalaryStructures();
    void createSalaryStructure(SalaryStructureDTO salaryStructureDTO);
    int createAllSalaryStructures(List<SalaryStructureDTO> structures);
    void deleteSalaryStructure(String name);
    int deleteAllSalaryStructures(List<String> names);
}