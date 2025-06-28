package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.GenderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderRepository extends JpaRepository<GenderDTO, String> {
}