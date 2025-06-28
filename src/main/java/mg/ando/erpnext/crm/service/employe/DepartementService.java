package mg.ando.erpnext.crm.service.employe;

import mg.ando.erpnext.crm.dto.DepartementDTO;
import java.util.List;

public interface DepartementService {
    DepartementDTO getByName(String name);
    DepartementDTO getDepartementByName(String name);
    List<DepartementDTO> getAllDepartements();
}

