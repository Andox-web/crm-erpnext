package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructureDTO, String> {
}