package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryAssignmentRepository extends JpaRepository<SalaryAssignmentDTO, String> {
}