package mg.ando.erpnext.crm.service.employe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.CompanyDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final ErpRestService erpRestService;
    private static final String COMPANY_ENDPOINT = "/api/resource/Company";

    // Liste des champs à récupérer par défaut
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "company_name",
        "default_currency",
    };

    public CompanyServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public CompanyDTO getCompanyByName(String name) {
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
    public void createCompany(CompanyDTO companyDTO) {
     
        erpRestService.callApi(
            COMPANY_ENDPOINT,
            HttpMethod.POST,
            companyDTO,
            null,
            Object.class
        );
    }

    @Override
    public int createAllCompanies(List<CompanyDTO> companyDTOs) {
        if (companyDTOs == null || companyDTOs.isEmpty()) {
            return 0;
        }

        List<Map<String, Object>> docs = new ArrayList<>();
        for (CompanyDTO dto : companyDTOs) {
            Map<String, Object> companyMap = new HashMap<>();
            companyMap.put("doctype", "Company");
            companyMap.put("name", dto.getName());
            companyMap.put("company_name", dto.getCompanyName());
            companyMap.put("default_currency", dto.getDefaultCurrency());
            
            docs.add(companyMap);
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
            System.err.println("Erreur création en masse des sociétés : " + e.getMessage());
        }

        throw new RuntimeException("Erreur lors de la création en masse des sociétés");
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
        if (companyNames == null || companyNames.isEmpty()) {
            return 0;
        }

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
}