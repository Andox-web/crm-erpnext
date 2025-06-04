package mg.ando.erpnext.crm.service.salary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {

    private final ErpRestService erpRestService;
    private static final String SALARY_SLIP_ENDPOINT = "/api/resource/Salary Slip";

    // Champs par défaut pour les Salary Slips
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "employee",
        "employee_name",
        "start_date",
        "end_date",
        "posting_date",
        "company",
        "currency",
        "gross_pay",
        "net_pay",
        "status",
        "total_deduction",
        "salary_structure"
    };

    public SalarySlipServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public SalarySlipDTO getSalarySlipByName(String name) {
        String endpoint = SALARY_SLIP_ENDPOINT + "/" + name;
        return erpRestService.callErpApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            SalarySlipDTO.class
        );
    }
    @Override
    public List<SalarySlipDTO> getSalarySlipsByEmployee(String employeeName) {
        HttpHeaders headers = null;

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("employee", "=", employeeName));

        SalarySlipDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.GET,
            null,
            headers,
            DEFAULT_FIELDS,
            filters,
            SalarySlipDTO[].class
        );

        return result != null ? List.of(result) : Collections.emptyList();
    }
    @Override
    public List<SalarySlipDTO> getAllSalarySlips() {
        SalarySlipDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            SalarySlipDTO[].class
        );
        
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public void createSalarySlip(SalarySlipDTO salarySlipDTO) {
        erpRestService.callErpApi(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.POST,
            salarySlipDTO,
            null,
            Object.class
        );
    }

    @Override
    public int createAllSalarySlips(List<SalarySlipDTO> salarySlipDTOs) {
        if (salarySlipDTOs == null || salarySlipDTOs.isEmpty()) {
            return 0;
        }

        List<Map<String, Object>> docs = new ArrayList<>();
        for (SalarySlipDTO dto : salarySlipDTOs) {
            Map<String, Object> slipMap = new HashMap<>();
            slipMap.put("doctype", "Salary Slip");
            slipMap.put("name", dto.getName());
            slipMap.put("employee", dto.getEmployee());
            slipMap.put("employee_name", dto.getEmployeeName());
            slipMap.put("start_date", dto.getStartDate());
            slipMap.put("company", dto.getCompany());
            slipMap.put("salary_structure", dto.getSalaryStructure());
            docs.add(slipMap);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("docs", docs);

        try {
            Map<String, Object> response = erpRestService.callErpApi(
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
            System.err.println("Erreur création en masse des bulletins : " + e.getMessage());
        }

        throw new RuntimeException("Erreur lors de la création en masse des bulletins");
    }

    @Override
    public void deleteSalarySlip(String name) {
        String endpoint = SALARY_SLIP_ENDPOINT + "/" + name;
        erpRestService.callErpApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            null,
            Object.class
        );
    }

    @Override
    public int deleteAllSalarySlips(List<String> salarySlipNames) {
        if (salarySlipNames == null || salarySlipNames.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (String name : salarySlipNames) {
            try {
                deleteSalarySlip(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression bulletin " + name, e);
            }
        }
        return count;
    }
}