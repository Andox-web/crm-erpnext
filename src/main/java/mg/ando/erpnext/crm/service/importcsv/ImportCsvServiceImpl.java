package mg.ando.erpnext.crm.service.importcsv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import mg.ando.erpnext.crm.config.DateConverter;
import mg.ando.erpnext.crm.config.GenderNormalizer;
import mg.ando.erpnext.crm.config.Util;
import mg.ando.erpnext.crm.dto.CompanyDTO;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import mg.ando.erpnext.crm.dto.SalaryComponentDTO;
import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service    
public class ImportCsvServiceImpl implements ImportCsvService {

    private final ErpRestService erpRestService;
    private final ObjectMapper objectMapper;

    public ImportCsvServiceImpl(ErpRestService erpRestService,ObjectMapper objectMapper) {
        this.erpRestService = erpRestService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void importCsv(MultipartFile employeCsv, MultipartFile salaryStructureCsv , MultipartFile salarySlipCsv) throws IOException {
        if (employeCsv == null || employeCsv.isEmpty()) {
            throw new IllegalArgumentException("Le fichier CSV des employés ne peut pas être vide.");
        }
        if (salaryStructureCsv == null || salaryStructureCsv.isEmpty()) {
            throw new IllegalArgumentException("Le fichier CSV des structures salariales ne peut pas être vide.");
        }

        if (salarySlipCsv != null && salarySlipCsv.isEmpty()) {
            throw new IllegalArgumentException("Le fichier CSV des fiches de paie n'est pas encore implémenté.");
        }
        
        Map<Integer,CSVRecord> employeRecords = Util.parseCsv(employeCsv);
        Map<Integer,CSVRecord> salaryStructureRecords = Util.parseCsv(salaryStructureCsv);
        Map<Integer,CSVRecord> salarySlipRecords = Util.parseCsv(salarySlipCsv);
        
        Map<String, EmployeDTO> employes = new HashMap<>();
        Map<String, CompanyDTO> companies = new HashMap<>();
        
        Map<String, SalaryStructureDTO> salaryStructures = new HashMap<>();
        Map<String, SalaryComponentDTO> salaryComponents = new HashMap<>();
        
        Set<String> fiscalYears = new HashSet<>();
        Map<String, SalarySlipDTO> salarySlips = new HashMap<>();
        Map<String, SalaryAssignmentDTO> salaryAssignments = new HashMap<>();
        

        List<String> allErrors = new ArrayList<>();
        for (Map.Entry<Integer,CSVRecord> employeRecord : employeRecords.entrySet()) {
            // Ref	Nom	Prenom	genre	Date embauche	date naissance	company
            try{
                CSVRecord record = employeRecord.getValue();
                EmployeDTO employe = new EmployeDTO();
                employe.setEmployeeNumber(record.get("Ref"));
                if (employes.containsKey(employe.getEmployeeNumber())) {
                    throw new ImportCsvException("L'employé avec le ref " + employe.getEmployeeNumber() + " existe déjà.");
                }
                employe.setFirstName(record.get("Prenom"));
                employe.setLastName(record.get("Nom"));
                employe.setGender(GenderNormalizer.normalizeGender(record.get("genre")));
                employe.setDateOfJoining(DateConverter.convertToIsoDate(record.get("Date embauche"))); 
                employe.setDateOfBirth(DateConverter.convertToIsoDate(record.get("date naissance")));
                employe.setCompany(record.get("company"));
                
                employes.put(employe.getEmployeeNumber(), employe);

                if (!companies.containsKey(employe.getCompany())) {
                    CompanyDTO company = new CompanyDTO();
                    company.setName(employe.getCompany());
                    company.setCompanyName(employe.getCompany());
                    company.setDefaultCurrency("USD"); 
                    companies.put(company.getName(), company);
                }
            }catch (Exception e) {
                allErrors.add("Ligne " + employeRecord.getKey() + ": " + e.getMessage());
            }
        }
        
        if (!allErrors.isEmpty()) {
            throw new ImportCsvException(
                "Erreurs dans le fichier employé:\n" + 
                String.join("\n")
            );
        }
        for (Map.Entry<Integer,CSVRecord> salaryStructureRecord : salaryStructureRecords.entrySet()) {
            // salary structure	name	Abbr	type	valeur	company
            CSVRecord record = salaryStructureRecord.getValue();
            
            try{
                if (!salaryStructures.containsKey(record.get("salary structure"))) {
                    SalaryStructureDTO salaryStructure = new SalaryStructureDTO();
                    salaryStructure.setName(record.get("salary structure")); 
                    salaryStructure.setCompany(record.get("company"));
                    salaryStructure.setIsActive("Yes");
                    salaryStructure.setPayrollFrequency("Monthly");
                    
                    if (!companies.containsKey(salaryStructure.getCompany())) {
                        CompanyDTO company = new CompanyDTO();
                        company.setName(salaryStructure.getCompany());
                        company.setCompanyName(salaryStructure.getCompany());
                        company.setDefaultCurrency("USD"); 
                        companies.put(company.getName(), company);
                    }    
                    salaryStructures.put(salaryStructure.getName(), salaryStructure);
                }
                
                SalaryComponentDTO salaryComponent = new SalaryComponentDTO();
                salaryComponent.setSalaryComponent(record.get("name"));
                if (salaryComponents.containsKey(salaryComponent.getAbbr())) {
                    System.err.println("Le composant de salaire avec l'abréviation " + salaryComponent.getAbbr() + " existe déjà.");
                    throw new ImportCsvException("Le composant de salaire avec l'abréviation " + salaryComponent.getAbbr() + " existe déjà.");
                }
                salaryComponent.setAbbr(record.get("Abbr"));
                salaryComponent.setAmountBasedOnFormula(true);
                salaryComponent.setFormula(record.get("valeur"));
                salaryComponent.setType(record.get("type").substring(0, 1).toUpperCase() + record.get("type").substring(1).toLowerCase());

                SalaryDetailDTO salaryDetail = new SalaryDetailDTO();
                salaryDetail.setSalaryComponent(salaryComponent.getSalaryComponent());
                salaryDetail.setAmountBasedOnFormula(true);
                salaryDetail.setFormula(record.get("valeur"));

                if (!salaryStructures.containsKey(record.get("salary structure"))) {
                    System.err.println("Salaire de structure " + salaryComponent.getAbbr() + " n\'existe pas.");
                    throw new ImportCsvException("Salaire de structure " + salaryComponent.getAbbr() + " n\'existe pas.");
                }
                SalaryStructureDTO salaryStructure = salaryStructures.get(record.get("salary structure"));
                if (salaryComponent.getType().equals("Earning")) {
                    if (salaryStructure.getEarnings() == null) {
                        salaryStructure.setEarnings(new ArrayList<>());
                    }
                    salaryStructure.getEarnings().add(salaryDetail);
                } else if (salaryComponent.getType().equals("Deduction")) {
                    if (salaryStructure.getDeductions() == null) {
                        salaryStructure.setDeductions(new ArrayList<>());
                    }
                    salaryStructure.getDeductions().add(salaryDetail);
                } else {
                    System.err.println("Type de composant de salaire inconnu: " + salaryComponent.getType());
                    throw new ImportCsvException("Type de composant de salaire inconnu: " + salaryComponent.getType());
                }

                salaryComponents.put(salaryComponent.getAbbr(), salaryComponent);


            }catch (Exception e) {
                allErrors.add("Ligne " + salaryStructureRecord.getKey() + ": " + e.getMessage());
            }
            
        }
        if (!allErrors.isEmpty()) {
            throw new ImportCsvException(
                "Erreurs dans le fichier des structures salariales: " + 
                String.join(" | ", allErrors)
            );
        }

        for (Map.Entry<Integer,CSVRecord> salarySlipRecord : salarySlipRecords.entrySet()) {
            // Mois	Ref Employe	Salaire Base	Salaire
            CSVRecord record = salarySlipRecord.getValue();
            try{    
                if (record.get("Ref Employe") == null || record.get("Ref Employe").isEmpty() || !employes.containsKey(record.get("Ref Employe"))) {
                    System.err.println("L'enregistrement à la ligne " + salarySlipRecord.getKey() + " n'a pas de référence d'employé.");
                    throw new ImportCsvException("L'enregistrement à la ligne " + salarySlipRecord.getKey() + " n'a pas de référence d'employé ("+record.get("Employe")+").");       
                }
                if (record.get("Salaire") == null || record.get("Salaire").isEmpty() || !salaryStructures.containsKey(record.get("Salaire"))) {
                    System.err.println("L'enregistrement à la ligne " + salarySlipRecord.getKey() + " n'a pas de référence de salaire.");
                    throw new ImportCsvException("L'enregistrement à la ligne " + salarySlipRecord.getKey() + " n'a pas de référence de salaire ("+record.get("Salaire")+").");
                }
                
                EmployeDTO employe = employes.get(record.get("Ref Employe"));
                String startDate = DateConverter.getStartDateOfMonth(record.get("Mois"));
                String endDate = DateConverter.getEndDateOfMonth(record.get("Mois"));
                // Ajouter l'année fiscale à fiscalYears
                String fiscalYear = DateConverter.getYear(startDate);
                fiscalYears.add(fiscalYear);
                fiscalYear= DateConverter.getYear(endDate);
                fiscalYears.add(fiscalYear);


                SalaryAssignmentDTO salaryAssignment = new SalaryAssignmentDTO();
                salaryAssignment.setEmployee(record.get("Ref Employe"));
                salaryAssignment.setSalaryStructure(record.get("Salaire"));
                salaryAssignment.setCompany(employe.getCompany());
                salaryAssignment.setFromDate(startDate);
                salaryAssignment.setToDate(endDate);
                salaryAssignment.setBase(Double.valueOf(record.get("Salaire Base")));
                
                SalarySlipDTO salarySlip = new SalarySlipDTO();
                salarySlip.setEmployee(employes.get(record.get("Ref Employe")).getName());
                salarySlip.setStartDate(startDate);
                salarySlip.setEndDate(endDate);

                salarySlip.setEmployee(employe.getEmployeeNumber());
                salarySlip.setCompany(employe.getCompany());
                salarySlip.setEmployeeName(employe.getEmployeeName());
                salarySlip.setSalaryStructure(record.get("Salaire"));

                salaryAssignments.put(salaryAssignment.getEmployee() + salaryAssignment.getSalaryStructure() + salaryAssignment.getFromDate(), salaryAssignment);
                salarySlips.put(salarySlip.getEmployee()+salarySlip.getStartDate()+salarySlip.getSalaryStructure(), salarySlip);

            }catch (Exception e) {
                allErrors.add("Ligne " + salarySlipRecord.getKey() + ": " + e.getMessage());
            }
        }

        if(!allErrors.isEmpty()){
            throw new ImportCsvException(
                "Erreurs dans le fichier des fiches de paie: |==> " + 
                String.join(" |==> ", allErrors)
            );
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("companies", objectMapper.writeValueAsString(new ArrayList<>(companies.values())));
        requestBody.put("employes", objectMapper.writeValueAsString(new ArrayList<>(employes.values())));
        // Correction des clés ici :
        requestBody.put("salary_structures", objectMapper.writeValueAsString(new ArrayList<>(salaryStructures.values())));
        requestBody.put("salary_components", objectMapper.writeValueAsString(new ArrayList<>(salaryComponents.values())));

        // Trier salaryAssignments par fromDate puis toDate
        List<SalaryAssignmentDTO> sortedSalaryAssignments = new ArrayList<>(salaryAssignments.values());
        sortedSalaryAssignments.sort((a, b) -> {
            try {
            java.time.LocalDate aFrom = java.time.LocalDate.parse(a.getFromDate());
            java.time.LocalDate bFrom = java.time.LocalDate.parse(b.getFromDate());
            int cmp = aFrom.compareTo(bFrom);
            if (cmp == 0) {
                java.time.LocalDate aTo = java.time.LocalDate.parse(a.getToDate());
                java.time.LocalDate bTo = java.time.LocalDate.parse(b.getToDate());
                return aTo.compareTo(bTo);
            }
            return cmp;
            } catch (Exception ex) {
                // fallback to string compare if parsing fails
                int cmp = a.getFromDate().compareTo(b.getFromDate());
                if (cmp == 0) {
                    return a.getToDate().compareTo(b.getToDate());
                }
                return cmp;
            }
        });
        requestBody.put("salary_assignments", objectMapper.writeValueAsString(sortedSalaryAssignments));

        requestBody.put("fiscal_years", objectMapper.writeValueAsString(fiscalYears));

        // Trier salarySlips par startDate (convertir en LocalDate pour comparer)
        List<SalarySlipDTO> sortedSalarySlips = new ArrayList<>(salarySlips.values());
        sortedSalarySlips.sort((a, b) -> {
            try {
                java.time.LocalDate aStart = java.time.LocalDate.parse(a.getStartDate());
                java.time.LocalDate bStart = java.time.LocalDate.parse(b.getStartDate());
                return aStart.compareTo(bStart);
                } catch (Exception ex) {
                return a.getStartDate().compareTo(b.getStartDate());
            }
        });
        requestBody.put("salary_slips", objectMapper.writeValueAsString(sortedSalarySlips));

        try {
            JsonNode response =  erpRestService.callApi(
                "/api/method/hrms.api.importData.import_data",
                HttpMethod.POST,
                requestBody,
                null,
                JsonNode.class
            ).get("message");
            
            if (response == null || response.isEmpty()) {
                throw new ImportCsvException("Aucune donnée n'a été importée."+response);
            }
            if (response.get("success") == null || !response.get("success").asBoolean()) {
                String errorMessage = response.get("error") != null ? response.get("error").asText() : "Erreur inconnue lors de l'importation des données.";
                System.err.println(errorMessage);
                throw new ImportCsvException(errorMessage);
            } else {
                System.out.println("Importation des données réussie.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'importation des données : " + e.getMessage());
            throw new ImportCsvException("Erreur lors de l'importation des données : " + e.getMessage(), e);
        }
    }
}