package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.EmployeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeRepository extends JpaRepository<EmployeDTO, String> {
}