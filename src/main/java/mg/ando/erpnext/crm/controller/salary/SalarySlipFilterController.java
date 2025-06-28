package mg.ando.erpnext.crm.controller.salary;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.service.salary.SalaryComponentService;
import mg.ando.erpnext.crm.service.salary.SalarySlipFilterService;
import mg.ando.erpnext.crm.service.salary.SalarySlipService;


@Controller
@RequestMapping("/salarySlipFilter")
public class SalarySlipFilterController {

    SalarySlipFilterService salarySlipFilterService;
    SalarySlipService salarySlipService;
    SalaryComponentService salaryComponentService;

    

    public SalarySlipFilterController(SalarySlipFilterService salarySlipFilterService,
            SalarySlipService salarySlipService, SalaryComponentService salaryComponentService) {
        this.salarySlipFilterService = salarySlipFilterService;
        this.salarySlipService = salarySlipService;
        this.salaryComponentService = salaryComponentService;
    }



    @GetMapping
    public String getSalarySlipFilter(
         @RequestParam(name = "composant",required = false) String composant,
        @RequestParam(name = "operateur" ,required = false) String operateur,
        @RequestParam(name = "valeur" ,required = false) Double valeur,
        Model model
    ) {
        List<SalarySlipDTO> salarySlipDTOs ;
        if (composant == null || composant.isEmpty() || operateur == null || operateur.isEmpty() || valeur == null || valeur.equals(0.0)) {
            salarySlipDTOs = salarySlipService.getAllSalarySlips();
        }
        else{
            salarySlipDTOs = salarySlipFilterService.getSalarySlipFilter(composant, operateur, valeur);
        }
        model.addAttribute("salarySlips", salarySlipDTOs);
        model.addAttribute("components",salaryComponentService.getAllSalaryComponents());
        return "salary/salarySlipFilter"; 
    }
    
}
