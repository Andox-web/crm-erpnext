package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;

import java.util.List;

public interface SalaryAssignmentService {
    SalaryAssignmentDTO getByName(String name);
    List<SalaryAssignmentDTO> getAll();
    void create(SalaryAssignmentDTO dto);
    int createAll(List<SalaryAssignmentDTO> dtos);
    void delete(String name);
    int deleteAll(List<String> names);
}
