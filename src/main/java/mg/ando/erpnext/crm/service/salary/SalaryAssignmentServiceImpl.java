package mg.ando.erpnext.crm.service.salary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class SalaryAssignmentServiceImpl implements SalaryAssignmentService {

    private final ErpRestService erpRestService;
    private static final String ENDPOINT = "/api/resource/Salary Structure Assignment";

    // Champs par défaut pour les requêtes
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "employee",
        "employee_name",
        "company",
        "salary_structure",
        "base",
        "variable",
        "from_date",
        "currency"
    };

    public SalaryAssignmentServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public SalaryAssignmentDTO getByName(String name) {
        String endpoint = ENDPOINT + "/" + name;
        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            SalaryAssignmentDTO.class
        );
    }

    @Override
    public List<SalaryAssignmentDTO> getAll() {
        SalaryAssignmentDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            SalaryAssignmentDTO[].class,
            "limit_page_length=10000"
        );
        
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public SalaryAssignmentDTO create(SalaryAssignmentDTO dto) {
        return erpRestService.callApi(
            ENDPOINT,
            HttpMethod.POST,
            dto,
            null,
            SalaryAssignmentDTO.class
        );
    }

    @Override
    public List<SalaryAssignmentDTO> createAll(List<SalaryAssignmentDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return new ArrayList<>();

        List<Map<String, Object>> docs = new ArrayList<>();
        for (SalaryAssignmentDTO dto : dtos) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("doctype", "Salary Structure Assignment");
            doc.put("employee", dto.getEmployee());
            doc.put("employee_name", dto.getEmployeeName());
            doc.put("company", dto.getCompany());
            doc.put("salary_structure", dto.getSalaryStructure());
            doc.put("base", dto.getBase());
            doc.put("variable", dto.getVariable());
            doc.put("from_date", dto.getFromDate());
            doc.put("to_date", dto.getToDate());
            doc.put("currency", dto.getCurrency());
            docs.add(doc);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("docs", docs);

        try {
            TypeReference<List<String>> typeRef = new TypeReference<>() {};
            List<String> result = erpRestService.callApi(
                "/api/method/frappe.client.insert_many",
                HttpMethod.POST,
                requestBody,
                null,
                typeRef,
                ErpRestService.ApiOptions.builder().dataPath("message").build()
            );

            if (result != null) {
                // Tu peux ensuite récupérer chaque SalaryAssignmentDTO par son nom
                List<SalaryAssignmentDTO> created = new ArrayList<>();
                for (String name : result) {
                    created.add(getByName(name)); // appel à l'ERP pour charger le doc complet
                }
                return created;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur création en masse des assignments", e);
        }
        throw new RuntimeException("Erreur création en masse des assignments");
        
    }

    @Override
    public void delete(String name) {
        String endpoint = ENDPOINT + "/" + name;
        erpRestService.callApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            null,
            Object.class
        );
    }

    @Override
    public int deleteAll(List<String> names) {
        if (names == null || names.isEmpty()) return 0;
        
        int count = 0;
        for (String name : names) {
            try {
                delete(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression assignment " + name, e);
            }
        }
        return count;
    }

    @Override
    public boolean cancel(SalaryAssignmentDTO salaryAssignmentDTO) {
        try {
            erpRestService.callApiWithResponse(ENDPOINT+"/"+salaryAssignmentDTO.getName()
            , HttpMethod.POST, 
             null,
             null,
              Void.class, "run_method=cancel");
              return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean submit(SalaryAssignmentDTO salaryAssignmentDTO) {
        try {
            erpRestService.callApiWithResponse(ENDPOINT+"/"+salaryAssignmentDTO.getName()
            , HttpMethod.POST, 
             null,
             null,
              Void.class, "run_method=submit");
              return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SalaryAssignmentDTO> getWithFilters(List<Filter> filters) {
        SalaryAssignmentDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            SalaryAssignmentDTO[].class,
            "limit_page_length=10000"
        );
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }
}