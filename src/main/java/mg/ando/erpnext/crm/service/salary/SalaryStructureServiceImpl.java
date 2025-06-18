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
import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final ErpRestService erpRestService;
    private static final String SALARY_STRUCTURE_ENDPOINT = "/api/resource/Salary Structure";

    // Champs par défaut pour les requêtes GET (sans les tables enfants)
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
        String endpoint = SALARY_STRUCTURE_ENDPOINT + "/" + name;
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
            SALARY_STRUCTURE_ENDPOINT,
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
    public void createSalaryStructure(SalaryStructureDTO salaryStructureDTO) {
        Map<String, Object> requestBody = buildSalaryStructureBody(salaryStructureDTO);
        
        erpRestService.callApi(
            SALARY_STRUCTURE_ENDPOINT,
            HttpMethod.POST,
            requestBody,
            null,
            Object.class
        );
    }

    @Override
    public int createAllSalaryStructures(List<SalaryStructureDTO> structures) {
        if (structures == null || structures.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (SalaryStructureDTO dto : structures) {
            try {
                createSalaryStructure(dto);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur création structure salariale " + dto.getName(), e);
            }
        }
        return count;
    }

    @Override
    public void deleteSalaryStructure(String name) {
        String endpoint = SALARY_STRUCTURE_ENDPOINT + "/" + name;
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
        if (names == null || names.isEmpty()) {
            return 0;
        }

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

    // Méthode utilitaire pour construire le corps de la requête
    private Map<String, Object> buildSalaryStructureBody(SalaryStructureDTO dto) {
        Map<String, Object> doc = new HashMap<>();
        
        // Champs principaux
        doc.put("doctype", "Salary Structure");
        doc.put("name", dto.getName());
        doc.put("company", dto.getCompany());
        
        // Table enfant : Earnings
        List<Map<String, Object>> earningsList = new ArrayList<>();
        for (SalaryDetailDTO earning : dto.getEarnings()) {
            Map<String, Object> earningMap = new HashMap<>();
            earningMap.put("salary_component", earning.getSalaryComponent());
            earningsList.add(earningMap);
        }
        doc.put("earnings", earningsList);
        
        // Table enfant : Deductions
        List<Map<String, Object>> deductionsList = new ArrayList<>();
        for (SalaryDetailDTO deduction : dto.getDeductions()) {
            Map<String, Object> deductionMap = new HashMap<>();
            deductionMap.put("salary_component", deduction.getSalaryComponent());
            // Ajouter d'autres champs si nécessaire
            deductionsList.add(deductionMap);
        }
        doc.put("deductions", deductionsList);
        
        return doc;
    }

    @Override
    public List<SalaryStructureDTO> getWithFilters(List<Filter> filters) {
        SalaryStructureDTO[] result = erpRestService.callApiWithFilters(
            SALARY_STRUCTURE_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            SalaryStructureDTO[].class
        );
        
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }
}