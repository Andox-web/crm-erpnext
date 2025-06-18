package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;

import java.util.List;

public interface SalaryAssignmentService {
    SalaryAssignmentDTO getByName(String name);
    List<SalaryAssignmentDTO> getAll();
    SalaryAssignmentDTO create(SalaryAssignmentDTO dto);
    List<SalaryAssignmentDTO> createAll(List<SalaryAssignmentDTO> dtos);
    void delete(String name);
    int deleteAll(List<String> names);
    boolean submit(SalaryAssignmentDTO salaryAssignmentDTO);
    boolean cancel(SalaryAssignmentDTO salaryAssignmentDTO);
    List<SalaryAssignmentDTO> getWithFilters(List<Filter> filters);
}
