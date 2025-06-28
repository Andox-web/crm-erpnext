package mg.ando.erpnext.crm.service.employe;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.service.ErpRestService;
import mg.ando.erpnext.crm.service.ErpRestService.ApiOptions;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeServiceImpl implements EmployeService {

    private final ErpRestService erpRestService;
    private static final String EMPLOYEE_ENDPOINT = "/api/resource/Employee";

    private static final String[] DEFAULT_FIELDS = {
        "name",
        "employee_name",
        "first_name",
        "last_name",
        "gender",
        "department",
        "designation",
        "date_of_joining",
        "status",
        "company",
        "employee_number"
    };

    public EmployeServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public EmployeDTO getEmployeByName(String name) {
        return getByName(name);
    }

    public EmployeDTO getByName(String name) {
        String endpoint = EMPLOYEE_ENDPOINT + "/" + name;
        return erpRestService.callApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            EmployeDTO.class
        );
    }

    @Override
    public List<EmployeDTO> getAllEmployes() {
        EmployeDTO[] result = erpRestService.callApiWithFilters(
            EMPLOYEE_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            EmployeDTO[].class,
            "limit_page_length=10000"
        );

        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public EmployeDTO createEmploye(EmployeDTO employeDTO) {
        return erpRestService.callApi(
            EMPLOYEE_ENDPOINT,
            HttpMethod.POST,
            employeDTO,
            null,
            EmployeDTO.class
        );
    }

    @Override
    public List<EmployeDTO> createAllEmployes(List<EmployeDTO> employeDTOs) {
        if (employeDTOs == null || employeDTOs.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> docs = new ArrayList<>();
        for (EmployeDTO dto : employeDTOs) {
            Map<String, Object> employeMap = new HashMap<>();
            employeMap.put("doctype", "Employee");
            employeMap.put("name", dto.getName());
            employeMap.put("first_name", dto.getFirstName());
            employeMap.put("employee_name", dto.getEmployeeName());
            employeMap.put("company", dto.getCompany());
            employeMap.put("date_of_joining", dto.getDateOfJoining());
            employeMap.put("last_name", dto.getLastName());
            employeMap.put("gender", dto.getGender());
            employeMap.put("status", dto.getStatus());
            employeMap.put("employee_number", dto.getEmployeeNumber());
            docs.add(employeMap);
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
                List<EmployeDTO> created = new ArrayList<>();
                for (String name : result) {
                    created.add(getByName(name));
                }
                return created;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création en masse des employés", e);
        }

        throw new RuntimeException("Erreur lors de la création en masse des employés");
    }

    @Override
    public void deleteEmploye(String name) {
        String endpoint = EMPLOYEE_ENDPOINT + "/" + name;
        erpRestService.callApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            null,
            Object.class
        );
    }

    @Override
    public int deleteAllEmployes(List<String> employeNames) {
        if (employeNames == null || employeNames.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (String name : employeNames) {
            try {
                deleteEmploye(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la suppression de l'employé " + name, e);
            }
        }
        return count;
    }

    @Override
    public List<EmployeDTO> getWithFilters(List<Filter> filters) {
        EmployeDTO[] result = erpRestService.callApiWithFilters(
            EMPLOYEE_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            EmployeDTO[].class,
            "limit_page_length=10000"
        );

        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public List<EmployeDTO> rechercherEmployes(Map<String, String> filtre) {
        List<Filter> filters = new ArrayList<>();

        if (filtre != null) {
            for (Map.Entry<String, String> entry : filtre.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key == null || value == null || key.isEmpty() || value.isEmpty()) continue;

                if (key.equals("employee_name")) {
                    filters.add(new Filter("employee_name", "like", "%" + value + "%"));
                } else if (key.equals("min_date_of_joining")) {
                    filters.add(new Filter("date_of_joining", ">=", value));
                } else if (key.equals("max_date_of_joining")) {
                    filters.add(new Filter("date_of_joining", "<=", value));
                } else {
                    filters.add(new Filter(key, "=", value));
                }
            }
        }

        return getWithFilters(filters);
    }
}
