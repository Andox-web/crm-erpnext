package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.DepartementDTO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartementRepository extends JpaRepository<DepartementDTO, String> {
    
    List<DepartementDTO> findByCompany(String company);
}