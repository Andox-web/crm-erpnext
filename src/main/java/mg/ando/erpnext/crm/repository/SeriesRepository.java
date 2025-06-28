package mg.ando.erpnext.crm.repository;

import mg.ando.erpnext.crm.dto.SeriesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SeriesRepository extends JpaRepository<SeriesDTO, String> {
    
    @Transactional
    @Modifying
    @Query("UPDATE SeriesDTO s SET s.current = s.current + 1 WHERE s.name = :name")
    int incrementSeries(String name);
}