package mg.ando.erpnext.crm.service.salary;

import java.util.*;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import mg.ando.erpnext.crm.service.ErpRestService;
import mg.ando.erpnext.crm.service.ErpRestService.ApiOptions;

@Service
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final ErpRestService erpRestService;
    private static final String ENDPOINT = "/api/resource/Salary Structure";

    private static final String[] DEFAULT_FIELDS = {
        "name",
        "company",
        "currency"
    };

    public SalaryStructureServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public SalaryStructureDTO getSalaryStructureByName(String name) {
        return getByName(name);
    }

    public SalaryStructureDTO getByName(String name) {
        String endpoint = ENDPOINT + "/" + name;
        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            SalaryStructureDTO.class
        );
    }

    @Override
    public List<SalaryStructureDTO> getAllSalaryStructures() {
        SalaryStructureDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            SalaryStructureDTO[].class,
            "limit_page_length=10000"
        );
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public SalaryStructureDTO createSalaryStructure(SalaryStructureDTO dto) {
        Map<String, Object> requestBody = buildSalaryStructureBody(dto);
        return erpRestService.callApi(
            ENDPOINT,
            HttpMethod.POST,
            requestBody,
            null,
            SalaryStructureDTO.class
        );
    }

    @Override
    public List<SalaryStructureDTO> createAllSalaryStructures(List<SalaryStructureDTO> structures) {
        if (structures == null || structures.isEmpty()) return new ArrayList<>();

        List<Map<String, Object>> docs = new ArrayList<>();
        for (SalaryStructureDTO dto : structures) {
            Map<String, Object> doc = buildSalaryStructureBody(dto);
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
                List<SalaryStructureDTO> created = new ArrayList<>();
                for (String name : result) {
                    created.add(getByName(name));
                }
                return created;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur création en masse des structures salariales", e);
        }

        throw new RuntimeException("Erreur création en masse des structures salariales");
    }

    @Override
    public void deleteSalaryStructure(String name) {
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
    public int deleteAllSalaryStructures(List<String> names) {
        if (names == null || names.isEmpty()) return 0;

        int count = 0;
        for (String name : names) {
            try {
                deleteSalaryStructure(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression structure salariale " + name, e);
            }
        }
        return count;
    }

    private Map<String, Object> buildSalaryStructureBody(SalaryStructureDTO dto) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("doctype", "Salary Structure");
        doc.put("name", dto.getName());
        doc.put("company", dto.getCompany());

        List<Map<String, Object>> earningsList = new ArrayList<>();
        if (dto.getEarnings() != null) {
            for (SalaryDetailDTO earning : dto.getEarnings()) {
                Map<String, Object> earningMap = new HashMap<>();
                earningMap.put("salary_component", earning.getSalaryComponent());
                earningsList.add(earningMap);
            }
        }
        doc.put("earnings", earningsList);

        List<Map<String, Object>> deductionsList = new ArrayList<>();
        if (dto.getDeductions() != null) {
            for (SalaryDetailDTO deduction : dto.getDeductions()) {
                Map<String, Object> deductionMap = new HashMap<>();
                deductionMap.put("salary_component", deduction.getSalaryComponent());
                deductionsList.add(deductionMap);
            }
        }
        doc.put("deductions", deductionsList);

        return doc;
    }

    @Override
    public List<SalaryStructureDTO> getWithFilters(List<Filter> filters) {
        SalaryStructureDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            SalaryStructureDTO[].class,
            "limit_page_length=10000"
        );
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }
}
