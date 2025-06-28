package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.SalaryConditionHistoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryConditionHistoryRepository 
    extends JpaRepository<SalaryConditionHistoryDTO, Long> {
}