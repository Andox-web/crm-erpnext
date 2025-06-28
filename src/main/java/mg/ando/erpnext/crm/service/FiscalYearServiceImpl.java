package mg.ando.erpnext.crm.service;

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
import mg.ando.erpnext.crm.dto.FiscalYearDTO;

@Service
public class FiscalYearServiceImpl implements FiscalYearService {

    private final ErpRestService erpRestService;
    private static final String ENDPOINT = "/api/resource/Fiscal Year";

    // Champs par défaut pour les requêtes
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "year_start_date",
        "year_end_date",
        "is_short_year",
        "company"
    };

    public FiscalYearServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public FiscalYearDTO getByName(String name) {
        String endpoint = ENDPOINT + "/" + name;
        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            FiscalYearDTO.class
        );
    }

    @Override
    public List<FiscalYearDTO> getAll() {
        FiscalYearDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            FiscalYearDTO[].class,
            "limit_page_length=10000"
        );
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public FiscalYearDTO create(FiscalYearDTO dto) {
        return erpRestService.callApi(
            ENDPOINT,
            HttpMethod.POST,
            dto,
            null,
            FiscalYearDTO.class
        );
    }

    @Override
    public List<FiscalYearDTO> createAll(List<FiscalYearDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> docs = new ArrayList<>();
        for (FiscalYearDTO dto : dtos) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("doctype", "Fiscal Year");
            doc.put("year_start_date", dto.getYearStartDate());
            doc.put("year_end_date", dto.getYearEndDate());
            doc.put("is_short_year", dto.isShortYear());
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
                List<FiscalYearDTO> created = new ArrayList<>();
                for (String name : result) {
                    created.add(getByName(name));
                }
                return created;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur création en masse des années fiscales", e);
        }
        throw new RuntimeException("Erreur création en masse des années fiscales");
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
                throw new RuntimeException("Erreur suppression année fiscale " + name, e);
            }
        }
        return count;
    }

    @Override
    public List<FiscalYearDTO> getWithFilters(List<Filter> filters) {
        FiscalYearDTO[] result = erpRestService.callApiWithFilters(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            FiscalYearDTO[].class,
            "limit_page_length=10000"
        );
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }
}