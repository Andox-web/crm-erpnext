package mg.ando.erpnext.crm.service.salary;

import java.util.*;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryComponentDTO;
import mg.ando.erpnext.crm.service.ErpRestService;
import mg.ando.erpnext.crm.service.ErpRestService.ApiOptions;

@Service
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final ErpRestService erpRestService;
    private static final String ENDPOINT = "/api/resource/Salary Component";

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
        return getByName(name);
    }

    public SalaryComponentDTO getByName(String name) {
        String endpoint = ENDPOINT + "/" + name;
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
            ENDPOINT,
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
    public SalaryComponentDTO createSalaryComponent(SalaryComponentDTO dto) {
        Map<String, Object> requestBody = buildSalaryComponentBody(dto);
        return erpRestService.callApi(
            ENDPOINT,
            HttpMethod.POST,
            requestBody,
            null,
            SalaryComponentDTO.class
        );
    }

    @Override
    public List<SalaryComponentDTO> createAllSalaryComponents(List<SalaryComponentDTO> components) {
        if (components == null || components.isEmpty()) return new ArrayList<>();

        List<Map<String, Object>> docs = new ArrayList<>();
        for (SalaryComponentDTO dto : components) {
            Map<String, Object> doc = buildSalaryComponentBody(dto);
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
                ApiOptions.builder().dataPath("message").build()
            );

            if (result != null) {
                List<SalaryComponentDTO> created = new ArrayList<>();
                for (String name : result) {
                    created.add(getByName(name));
                }
                return created;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur création en masse des Salary Components", e);
        }

        throw new RuntimeException("Erreur création en masse des Salary Components");
    }

    @Override
    public void deleteSalaryComponent(String name) {
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
    public int deleteAllSalaryComponents(List<String> names) {
        if (names == null || names.isEmpty()) return 0;

        int count = 0;
        for (String name : names) {
            try {
                deleteSalaryComponent(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression Salary Component " + name, e);
            }
        }
        return count;
    }

    private Map<String, Object> buildSalaryComponentBody(SalaryComponentDTO dto) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("doctype", "Salary Component");
        doc.put("salary_component", dto.getSalaryComponent());
        doc.put("salary_component_abbr", dto.getAbbr());
        doc.put("type", dto.getType());
        return doc;
    }

    @Override
    public List<SalaryComponentDTO> getWithFilters(List<Filter> filters) {
        SalaryComponentDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
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
