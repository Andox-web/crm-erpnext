package mg.ando.erpnext.crm.service.employe;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeServiceImpl implements EmployeService {

    private final ErpRestService erpRestService;
    private static final String EMPLOYEE_ENDPOINT = "/api/resource/Employee";
    
    // Liste des champs à récupérer par défaut
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
        HttpHeaders headers = null;
        String endpoint = EMPLOYEE_ENDPOINT + "/" + name;

        return erpRestService.callErpApi(
            endpoint,
            HttpMethod.GET,
            null,
            headers,
            EmployeDTO.class
        );
    }

    @Override
    public List<EmployeDTO> rechercherEmployes(Map<String, String> filtre) {
        HttpHeaders headers = null;
        
        // Convertir Map<String, String> en Map<String, Object>
        Map<String, Object> filtersMap = new HashMap<>();
        if (filtre != null) {
            filtersMap.putAll(filtre);
        }

        List<Filter> filters = new ArrayList<>();
        for (Map.Entry<String, Object> entry : filtersMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            
            if (value == null || value.isEmpty() || key == null || key.isEmpty()) {
                continue;
            }
            
            if (key.equals("employee_name")) {
                filters.add(new Filter(key, "like", "%" + value + "%"));
            } 
            else if (key.equals("min_date_of_joining")) {
                filters.add(new Filter("date_of_joining", ">=", value));
            }
            else if (key.equals("max_date_of_joining")) {
                filters.add(new Filter("date_of_joining", "<=", value));
            }
            else {
                filters.add(new Filter(key, "=", value));
            }
        }

        // Appeler la méthode avec champs et filtres
        EmployeDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            EMPLOYEE_ENDPOINT,
            HttpMethod.GET,
            null,
            headers,
            DEFAULT_FIELDS,
            filters,
            EmployeDTO[].class
        );
        
        return result != null ? List.of(result) : Collections.emptyList();
    }

    @Override
    public List<EmployeDTO> getAllEmployes() {
        HttpHeaders headers = null;
        
        // Appeler la méthode avec champs par défaut
        EmployeDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            EMPLOYEE_ENDPOINT,
            HttpMethod.GET,
            null,
            headers,
            DEFAULT_FIELDS,
            null, // pas de filtres
            EmployeDTO[].class
        );
        
        return result != null ? List.of(result) : Collections.emptyList();
    }

    @Override
    public int createAllEmployes(List<EmployeDTO> employeDTOs) {
        if (employeDTOs == null || employeDTOs.isEmpty()) {
            return 0;
        }

        // Chaque employé doit contenir un champ "doctype"
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

        // Construire le corps de la requête
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("docs", docs);

        try {
            // Appel de l'API frappe.client.insert_many
            Map<String, Object> response = erpRestService.callErpApi(
                "/api/method/frappe.client.insert_many",
                HttpMethod.POST,
                requestBody,
                null,
                Map.class
            );

            // Vérifie la présence de la clé "message" dans la réponse
            if (response != null && response.containsKey("message")) {
                List<?> resultList = (List<?>) response.get("message");
                return resultList.size(); // nombre d'employés créés
            }
        } catch (Exception e) {
            // Log l'erreur si besoin
            System.err.println("Erreur lors de la création en masse des employés : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création en masse des employés", e);
        }

        throw new RuntimeException("Erreur lors de la création en masse des employés");
    }


    @Override
    public void createEmploye(EmployeDTO employeDTO) {
        HttpHeaders headers = null;
        erpRestService.callErpApi(
            EMPLOYEE_ENDPOINT,
            HttpMethod.POST,
            employeDTO,
            headers,
            Object.class // On suppose que la réponse n'a pas besoin d’être typée ici
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
                // Log l'erreur si besoin
                throw new RuntimeException("Erreur lors de la suppression de l'employé " + name, e);
            }
        }
        return count;
    }


    @Override
    public void deleteEmploye(String name) {
        HttpHeaders headers = null;
        String endpoint = EMPLOYEE_ENDPOINT + "/" + name;

        erpRestService.callErpApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            headers,
            Object.class
        );
    }
}