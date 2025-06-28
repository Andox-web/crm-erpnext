package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalarySlipRepository extends JpaRepository<SalarySlipDTO, String> {
}