package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.FiscalYearDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiscalYearRepository extends JpaRepository<FiscalYearDTO, String> {

}