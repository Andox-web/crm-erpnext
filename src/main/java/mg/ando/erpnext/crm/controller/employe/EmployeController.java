package mg.ando.erpnext.crm.controller.employe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.dto.DepartementDTO;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.service.employe.DepartementService;
import mg.ando.erpnext.crm.service.employe.EmployeService;

@Controller
public class EmployeController {

    private final EmployeService employeService;
    private final DepartementService departementService;

    public EmployeController(EmployeService employeService, 
                            DepartementService departementService) {
        this.employeService = employeService;
        this.departementService = departementService;
    }

    @GetMapping("/employes")
    @RequireErpAuth
    public String listEmployes(
            @RequestParam(name = "employee_name", required = false) String nom,
            @RequestParam(name = "max_date_of_joining", required = false) String maxDateOfJoining,
            @RequestParam(name = "min_date_of_joining", required = false) String minDateOfJoining,
            Model model) {

        // 1. Récupérer tous les départements pour la liste déroulante
        List<DepartementDTO> departements = departementService.getAllDepartements();
        model.addAttribute("departements", departements);

        // 2. Construire les filtres
        Map<String, String> filters = new HashMap<>();
        if (nom != null && !nom.isEmpty()) 
            filters.put("employee_name", nom);
        if (maxDateOfJoining != null && !maxDateOfJoining.isEmpty()) 
            filters.put("max_date_of_joining", maxDateOfJoining);
        if (minDateOfJoining != null && !minDateOfJoining.isEmpty()) {
            filters.put("min_date_of_joining", minDateOfJoining);
        }

        // 3. Récupérer les employés filtrés
        List<EmployeDTO> employes;
        if (filters.isEmpty()) {
            employes = employeService.getAllEmployes();
        } else {
            employes = employeService.rechercherEmployes(filters);
        }

        // 4. Ajouter les données au modèle
        model.addAttribute("employes", employes);
        
        // Pour pré-remplir le formulaire de filtre
        model.addAttribute("filter", filters);

        return "employe/employe";
    }

}
