package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import mg.ando.erpnext.crm.service.ErpRestService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

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
        "to_date",
        "currency",
        "payroll_frequency",
        "taxable_income"
    };

    public SalaryAssignmentServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public SalaryAssignmentDTO getByName(String name) {
        String endpoint = ENDPOINT + "/" + name;
        return erpRestService.callErpApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            SalaryAssignmentDTO.class
        );
    }

    @Override
    public List<SalaryAssignmentDTO> getAll() {
        SalaryAssignmentDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            SalaryAssignmentDTO[].class
        );
        
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public void create(SalaryAssignmentDTO dto) {
        erpRestService.callErpApi(
            ENDPOINT,
            HttpMethod.POST,
            dto,
            null,
            Object.class
        );
    }

    @Override
    public int createAll(List<SalaryAssignmentDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return 0;

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
            doc.put("payroll_frequency", dto.getPayrollFrequency());
            doc.put("taxable_income", dto.getTaxableIncome());
            docs.add(doc);
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
                return ((List<?>) response.get("message")).size();
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur création en masse des assignments", e);
        }
        return 0;
    }

    @Override
    public void delete(String name) {
        String endpoint = ENDPOINT + "/" + name;
        erpRestService.callErpApi(
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
}