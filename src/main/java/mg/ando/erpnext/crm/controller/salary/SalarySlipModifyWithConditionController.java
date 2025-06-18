package mg.ando.erpnext.crm.controller.salary;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.service.employe.EmployeService;
import mg.ando.erpnext.crm.service.salary.SalaryAssignmentServiceImpl;
import mg.ando.erpnext.crm.service.salary.SalaryComponentService;
import mg.ando.erpnext.crm.service.salary.SalarySlipConditionService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequireErpAuth
@RequestMapping("modifySalarySlipWithCondition")
public class SalarySlipModifyWithConditionController {
    EmployeService employeService;
    SalaryComponentService salaryComponentService;
    SalarySlipConditionService salarySlipConditionService;
    
    public SalarySlipModifyWithConditionController(EmployeService employeService, SalaryComponentService  salaryComponentService,SalarySlipConditionService salarySlipConditionService) {
        this.employeService = employeService;
        this.salaryComponentService = salaryComponentService;
        this.salarySlipConditionService = salarySlipConditionService;
    }


    @GetMapping
    public String modifyWithConditionPage(Model model){
        model.addAttribute("employes",employeService.getAllEmployes());
        model.addAttribute("components",salaryComponentService.getAllSalaryComponents());
        return "salary/modifyWithCondition";
    }
    @PostMapping
    public String applyConditions(
            @RequestParam("employes") List<String> employes,
            @RequestParam("base") Double base,
            @RequestParam("composant[]") List<String> composants,
            @RequestParam("operateur[]") List<String> operateurs,
            @RequestParam("valeur[]") List<Double> valeurs,
            RedirectAttributes redirectAttributes) {
        try {
            salarySlipConditionService.applyConditions(employes, base, composants, operateurs, valeurs);
            redirectAttributes.addFlashAttribute("message", "Conditions applied successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred while applying conditions: " + e.getMessage());
            
        }
        
        return "redirect:/modifySalarySlipWithCondition?success";
    }

}
