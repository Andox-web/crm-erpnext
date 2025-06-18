package mg.ando.erpnext.crm.service.salary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryComponentDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final ErpRestService erpRestService;
    private static final String SALARY_COMPONENT_ENDPOINT = "/api/resource/Salary Component";

    // Champs par défaut pour les requêtes GET
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "salary_component",
        "salary_component_abbr",
        "type"
    };

    public SalaryComponentServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public SalaryComponentDTO getSalaryComponentByName(String name) {
        String endpoint = SALARY_COMPONENT_ENDPOINT + "/" + name;
        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            SalaryComponentDTO.class
        );
    }

    @Override
    public List<SalaryComponentDTO> getAllSalaryComponents() {
        SalaryComponentDTO[] result = erpRestService.callApiWithFilters(
            SALARY_COMPONENT_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            SalaryComponentDTO[].class,
            "limit_page_length=10000"
        );
        
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public void createSalaryComponent(SalaryComponentDTO salaryComponentDTO) {
        Map<String, Object> requestBody = buildSalaryComponentBody(salaryComponentDTO);
        
        erpRestService.callApi(
            SALARY_COMPONENT_ENDPOINT,
            HttpMethod.POST,
            requestBody,
            null,
            Object.class
        );
    }

    @Override
    public int createAllSalaryComponents(List<SalaryComponentDTO> components) {
        if (components == null || components.isEmpty()) {
            return 0;
        }

        List<Map<String, Object>> docs = new ArrayList<>();
        for (SalaryComponentDTO dto : components) {
            Map<String, Object> componentMap = new HashMap<>();
            componentMap.put("doctype", "Salary Component");
            componentMap.put("salary_component", dto.getSalaryComponent());
            componentMap.put("salary_component_abbr", dto.getAbbr());
            componentMap.put("type", dto.getType());
            docs.add(componentMap);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("docs", docs);

        try {
            Map<String, Object> response = erpRestService.callApi(
                "/api/method/frappe.client.insert_many",
                HttpMethod.POST,
                requestBody,
                null,
                Map.class
            );

            if (response != null && response.containsKey("message")) {
                List<?> resultList = (List<?>) response.get("message");
                return resultList.size();
            }
        } catch (Exception e) {
            System.err.println("Erreur création en masse des composants salariaux : " + e.getMessage());
        }

        throw new RuntimeException("Erreur lors de la création en masse des composants salariaux");
    }

    @Override
    public void deleteSalaryComponent(String name) {
        String endpoint = SALARY_COMPONENT_ENDPOINT + "/" + name;
        erpRestService.callApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            null,
            Object.class
        );
    }

    @Override
    public int deleteAllSalaryComponents(List<String> names) {
        if (names == null || names.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (String name : names) {
            try {
                deleteSalaryComponent(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression composant salarial " + name, e);
            }
        }
        return count;
    }

    // Méthode utilitaire pour construire le corps de la requête
    private Map<String, Object> buildSalaryComponentBody(SalaryComponentDTO dto) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("doctype", "Salary Component");
        doc.put("salary_component", dto.getSalaryComponent());
        doc.put("abbr", dto.getAbbr());
        doc.put("type", dto.getType());
        return doc;
    }

    @Override
    public List<SalaryComponentDTO> getWithFilters(List<Filter> filters) {
        SalaryComponentDTO[] result = erpRestService.callApiWithFilters(
            SALARY_COMPONENT_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            SalaryComponentDTO[].class,
            "limit_page_length=10000"
        );
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }
}