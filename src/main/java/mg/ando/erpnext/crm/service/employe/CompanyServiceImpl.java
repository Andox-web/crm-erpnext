package mg.ando.erpnext.crm.service.employe;

import java.util.*;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.CompanyDTO;
import mg.ando.erpnext.crm.service.ErpRestService;
import mg.ando.erpnext.crm.service.ErpRestService.ApiOptions;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final ErpRestService erpRestService;
    private static final String COMPANY_ENDPOINT = "/api/resource/Company";

    private static final String[] DEFAULT_FIELDS = {
        "name",
        "company_name",
        "default_currency"
    };

    public CompanyServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public CompanyDTO getCompanyByName(String name) {
        return getByName(name);
    }

    public CompanyDTO getByName(String name) {
        String endpoint = COMPANY_ENDPOINT + "/" + name;
        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            CompanyDTO.class
        );
    }

    @Override
    public List<CompanyDTO> getAllCompanies() {
        CompanyDTO[] result = erpRestService.callApiWithFilters(
            COMPANY_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            CompanyDTO[].class,
            "limit_page_length=10000"
        );

        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public CompanyDTO createCompany(CompanyDTO dto) {
        Map<String, Object> requestBody = buildCompanyBody(dto);
        return erpRestService.callApi(
            COMPANY_ENDPOINT,
            HttpMethod.POST,
            requestBody,
            null,
            CompanyDTO.class
        );
    }

    @Override
    public List<CompanyDTO> createAllCompanies(List<CompanyDTO> companyDTOs) {
        if (companyDTOs == null || companyDTOs.isEmpty()) return new ArrayList<>();

        List<Map<String, Object>> docs = new ArrayList<>();
        for (CompanyDTO dto : companyDTOs) {
            docs.add(buildCompanyBody(dto));
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
                List<CompanyDTO> created = new ArrayList<>();
                for (String name : result) {
                    created.add(getByName(name));
                }
                return created;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur création en masse des sociétés", e);
        }

        throw new RuntimeException("Erreur création en masse des sociétés");
    }

    @Override
    public void deleteCompany(String name) {
        String endpoint = COMPANY_ENDPOINT + "/" + name;
        erpRestService.callApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            null,
            Object.class
        );
    }

    @Override
    public int deleteAllCompanies(List<String> companyNames) {
        if (companyNames == null || companyNames.isEmpty()) return 0;

        int count = 0;
        for (String name : companyNames) {
            try {
                deleteCompany(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression société " + name, e);
            }
        }
        return count;
    }

    @Override
    public List<CompanyDTO> getWithFilters(List<Filter> filters) {
        CompanyDTO[] result = erpRestService.callApiWithFilters(
            COMPANY_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            CompanyDTO[].class,
            "limit_page_length=10000"
        );

        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    private Map<String, Object> buildCompanyBody(CompanyDTO dto) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("doctype", "Company");
        doc.put("name", dto.getName());
        doc.put("company_name", dto.getCompanyName());
        doc.put("default_currency", dto.getDefaultCurrency());
        return doc;
    }
}
