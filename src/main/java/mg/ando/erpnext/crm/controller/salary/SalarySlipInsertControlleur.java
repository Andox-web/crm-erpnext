package mg.ando.erpnext.crm.controller.salary;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.service.employe.EmployeService;
import mg.ando.erpnext.crm.service.salary.SalarySlipService;

@Controller
@RequestMapping("/insertSalarySlip")
public class SalarySlipInsertControlleur {
    public EmployeService employeService;
    public SalarySlipService salarySlipService;
    public SalarySlipInsertControlleur(EmployeService employeService,SalarySlipService salarySlipService){
        this.salarySlipService = salarySlipService;
        this.employeService = employeService;
    }
    @GetMapping()
    @RequireErpAuth
    public String insertSalarySlipPage(Model model){
        model.addAttribute("employees",employeService.getAllEmployes());
        return "salary/insertSalarySlip";
    }
    @PostMapping
    @RequireErpAuth
    public String insertSalarySlip(
            @RequestParam("employee_name") String employeName,
            @RequestParam("base") String base,
            @RequestParam("dateDebut") String dateDebut,
            @RequestParam("dateFin") String dateFin,
            RedirectAttributes redirectAttributes
        ){
            System.out.println(employeName);
            System.out.println(dateDebut);
            try {
                salarySlipService.insertSalarySlipForEmployeInPeriod(employeName, LocalDate.parse(dateDebut), LocalDate.parse(dateFin), base);
                redirectAttributes.addFlashAttribute("message","insert fini avec succee");         
            } catch (Exception e) {
                // TODO: handle exception
                redirectAttributes.addFlashAttribute("message",e.getMessage());
                e.printStackTrace();
            }
        return "redirect:/insertSalarySlip";

    }
}

