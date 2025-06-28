package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryDetailRepository extends JpaRepository<SalaryDetailDTO, String> {
    
    @Query("SELECT s FROM SalaryDetailDTO s WHERE s.parent = :parent AND s.parentfield = :parentfield")
    List<SalaryDetailDTO> findByParentAndParentfield(
        @Param("parent") String parent, 
        @Param("parentfield") String parentfield
    );
}