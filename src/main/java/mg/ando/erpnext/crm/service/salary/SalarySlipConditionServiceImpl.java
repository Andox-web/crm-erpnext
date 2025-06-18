package mg.ando.erpnext.crm.service.salary;

import mg.ando.erpnext.crm.service.employe.EmployeService;
import mg.ando.erpnext.crm.service.salary.SalarySlipConditionService;
import mg.ando.erpnext.crm.service.salary.SalaryAssignmentService;
import mg.ando.erpnext.crm.service.salary.SalaryComponentService;
import mg.ando.erpnext.crm.service.salary.SalarySlipService;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import mg.ando.erpnext.crm.config.Filter;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalarySlipConditionServiceImpl implements SalarySlipConditionService {

    private final SalaryAssignmentService salaryAssignmentService;
    private final SalaryComponentService salaryComponentService;
    private final SalarySlipService salarySlipService;
    private final EmployeService employeService;

    public SalarySlipConditionServiceImpl(SalaryAssignmentService salaryAssignmentService,
                                         SalaryComponentService salaryComponentService,
                                         SalarySlipService salarySlipService,
                                         EmployeService employeService) {
        this.salaryAssignmentService = salaryAssignmentService;
        this.salaryComponentService = salaryComponentService;
        this.salarySlipService = salarySlipService;
        this.employeService = employeService;
    }

    @Override
    public void applyConditions(List<String> employes, Double base,
                               List<String> composants, List<String> operateurs, List<Double> valeurs) {
        
        if (employes == null || employes.isEmpty()) {
            throw new IllegalArgumentException("La liste des employés ne peut pas être vide");
        }
        
        if (composants == null || operateurs == null || valeurs == null || 
            composants.size() != operateurs.size() || composants.size() != valeurs.size()) {
            throw new IllegalArgumentException("Les listes de composants, opérateurs et valeurs doivent avoir la même taille");
        }
        
        for (String employe : employes) {
            List<SalarySlipDTO> salarySlips = salarySlipService.getSalarySlipsByEmployee(employe);
            
            List<SalarySlipDTO> filteredSlips = salarySlips.stream()
                .filter(slip -> satisfiesAllConditions(salarySlipService.getSalarySlipByName(slip.getName()), composants, operateurs, valeurs))
                .collect(Collectors.toList());
            
            for (SalarySlipDTO slip : filteredSlips) {
                processSlip(slip, employe, base);
            }
        }
    }
    
    private boolean satisfiesAllConditions(SalarySlipDTO slip, 
                                         List<String> composants, 
                                         List<String> operateurs, 
                                         List<Double> valeurs) {
        
        for (int i = 0; i < composants.size(); i++) {
            String composant = composants.get(i);
            String operateur = operateurs.get(i);
            Double valeur = valeurs.get(i);
            if (!satisfiesCondition(slip, composant, operateur, valeur)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean satisfiesCondition(SalarySlipDTO slip, 
                                      String composant, 
                                      String operateur, 
                                      Double valeur) {
        
        Double componentValue = slip.getEarnings().stream()
            .filter(earning -> earning.getSalaryComponent().equals(composant))
            .map(earning -> earning.getAmount())
            .findFirst()
            .orElse(null);
        
        if (componentValue == null) {
            componentValue = slip.getDeductions().stream()
                .filter(deduction -> deduction.getSalaryComponent().equals(composant))
                .map(deduction -> deduction.getAmount())
                .findFirst()
                .orElse(null);
        }
        
        if (componentValue == null) {
            return false;
        }
        switch (operateur) {
            case "=":
                return componentValue.equals(valeur);
            case ">":
                return componentValue > valeur;
            case ">=":
                return componentValue >= valeur;
            case "<":
                return componentValue < valeur;
            case "<=":
                return componentValue <= valeur;
            default:
                throw new IllegalArgumentException("Opérateur non supporté: " + operateur);
        }
    }
    
    private void processSlip(SalarySlipDTO slip, String employe, Double newBase) {
        try {
            salarySlipService.cancel(slip);
            
            salarySlipService.deleteSalarySlip(slip.getName());
            
            LocalDate startDate = LocalDate.parse(slip.getStartDate(), DateTimeFormatter.ISO_DATE);
            LocalDate endDate = LocalDate.parse(slip.getEndDate(), DateTimeFormatter.ISO_DATE);
            
            List<Filter> filters = new ArrayList<>();
            filters.add(new Filter("employee", "=", employe));
            filters.add(new Filter("from_date", ">=", startDate.toString()));
            filters.add(new Filter("from_date", "<=", endDate.toString()));
            
            List<SalaryAssignmentDTO> assignments = salaryAssignmentService.getWithFilters(filters);
            
            if (!assignments.isEmpty()) {

                SalaryAssignmentDTO assignment = assignments.get(0);
                
                for (SalaryAssignmentDTO salaryAssignmentDTO : assignments) {
                    salaryAssignmentService.cancel(salaryAssignmentDTO);
                
                    salaryAssignmentService.delete(salaryAssignmentDTO.getName());
                }
                
                SalaryAssignmentDTO newAssignment = new SalaryAssignmentDTO();
                newAssignment.setEmployee(assignment.getEmployee());
                newAssignment.setEmployeeName(assignment.getEmployeeName());
                newAssignment.setCompany(assignment.getCompany());
                newAssignment.setSalaryStructure(assignment.getSalaryStructure());
                newAssignment.setBase(newBase);
                newAssignment.setVariable(assignment.getVariable());
                newAssignment.setFromDate(assignment.getFromDate());
                newAssignment.setToDate(assignment.getToDate());
                newAssignment.setCurrency(assignment.getCurrency());
                newAssignment.setPayrollFrequency(assignment.getPayrollFrequency());
                newAssignment.setTaxableIncome(assignment.getTaxableIncome());
                
                SalaryAssignmentDTO createdAssignment = salaryAssignmentService.create(newAssignment);
                salaryAssignmentService.submit(createdAssignment);
            }
            
            SalarySlipDTO newSlip = new SalarySlipDTO();
            newSlip.setEmployee(slip.getEmployee());
            newSlip.setEmployeeName(slip.getEmployeeName());
            newSlip.setStartDate(slip.getStartDate());
            newSlip.setEndDate(slip.getEndDate());
            newSlip.setCompany(slip.getCompany());
            newSlip.setCurrency(slip.getCurrency());
            newSlip.setSalaryStructure(slip.getSalaryStructure());
            
            SalarySlipDTO createdSlip = salarySlipService.createSalarySlip(newSlip);
            salarySlipService.submit(createdSlip);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du traitement du bulletin " + slip.getName(), e);
        }
    }
}