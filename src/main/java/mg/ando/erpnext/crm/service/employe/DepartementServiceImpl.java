package mg.ando.erpnext.crm.service.employe;

import mg.ando.erpnext.crm.dto.DepartementDTO;
import mg.ando.erpnext.crm.service.ErpRestService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DepartementServiceImpl implements DepartementService {

    private final ErpRestService erpRestService;
    private static final String DEPARTMENT_ENDPOINT = "/api/resource/Department";
    
    // Liste des champs à récupérer par défaut
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "department_name",
        "parent_department",
        "is_group",
        "company"
    };

    public DepartementServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public DepartementDTO getDepartementByName(String name) {
        HttpHeaders headers = null;
        String endpoint = DEPARTMENT_ENDPOINT + "/" + name;

        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            headers,
            DepartementDTO.class
        );
    }

    @Override
    public List<DepartementDTO> getAllDepartements() {
        HttpHeaders headers = null;
        
        // Appeler la méthode avec champs par défaut
        DepartementDTO[] result = erpRestService.callApiWithFilters(
            DEPARTMENT_ENDPOINT,
            HttpMethod.GET,
            null,
            headers,
            DEFAULT_FIELDS,
            null, // pas de filtres
            DepartementDTO[].class,
            "limit_page_length=10000"
        );
        
        return result != null ? List.of(result) : Collections.emptyList();
    }
}