package mg.ando.erpnext.crm.service.salary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;

@Service
public class SalarySlipConditionServiceImpl implements SalarySlipConditionService {

    private final SalaryAssignmentService salaryAssignmentService;
    private final SalarySlipService salarySlipService;

    public SalarySlipConditionServiceImpl(SalaryAssignmentService salaryAssignmentService,
                                         SalarySlipService salarySlipService) {
        this.salaryAssignmentService = salaryAssignmentService;
        this.salarySlipService = salarySlipService;
    }

    @Override
    public void applyConditions(Double base,int sign,
                               List<String> composants, List<String> operateurs, List<Double> valeurs) {
        
        
        if (composants == null || operateurs == null || valeurs == null || 
            composants.size() != operateurs.size() || composants.size() != valeurs.size()) {
            throw new IllegalArgumentException("Les listes de composants, opérateurs et valeurs doivent avoir la même taille");
        }
        if (sign != 1 && sign != -1) {
            throw new IllegalArgumentException("Le signe doit être 1 ou -1");
        }
        List<SalarySlipDTO> salarySlips = salarySlipService.getAllSalarySlips();
        
        List<SalarySlipDTO> filteredSlips = salarySlips.stream()
            .filter(slip -> satisfiesAllConditions(salarySlipService.getSalarySlipByName(slip.getName()), composants, operateurs, valeurs))
            .collect(Collectors.toList());
        
        for (SalarySlipDTO slip : filteredSlips) {
            processSlip(slip, sign == 1 ? base : -base);
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
    
    public boolean satisfiesCondition(SalarySlipDTO slip, 
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
    
    private void processSlip(SalarySlipDTO slip, Double base) {
        try {
            salarySlipService.cancel(slip);
            
            salarySlipService.deleteSalarySlip(slip.getName());
            
            LocalDate startDate = LocalDate.parse(slip.getStartDate(), DateTimeFormatter.ISO_DATE);
            LocalDate endDate = LocalDate.parse(slip.getEndDate(), DateTimeFormatter.ISO_DATE);
            
            List<Filter> filters = new ArrayList<>();
            filters.add(new Filter("employee", "=", slip.getEmployee()));
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
                newAssignment.setBase(assignment.getBase() + (base*assignment.getBase()) / 100);
                newAssignment.setVariable(assignment.getVariable());
                newAssignment.setFromDate(assignment.getFromDate());
                newAssignment.setToDate(assignment.getToDate());
                newAssignment.setCurrency(assignment.getCurrency());
                
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