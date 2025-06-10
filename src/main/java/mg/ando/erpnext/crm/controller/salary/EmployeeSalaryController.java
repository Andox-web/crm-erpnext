package mg.ando.erpnext.crm.controller.salary;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lowagie.text.DocumentException;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.service.employe.EmployeService;
import mg.ando.erpnext.crm.service.salary.PdfService;
import mg.ando.erpnext.crm.service.salary.SalarySlipService;

@Controller
@RequestMapping("/salaries")
public class EmployeeSalaryController {

    private final EmployeService employeService;
    private final SalarySlipService salarySlipService;
    private final PdfService pdfService;

    public EmployeeSalaryController(EmployeService employeService, SalarySlipService salarySlipService, PdfService pdfService) {
        this.employeService = employeService;
        this.salarySlipService = salarySlipService;
        this.pdfService = pdfService;
    }

    @GetMapping("/employee/{employeeName}")
    @RequireErpAuth
    public String getEmployeeSalaries(
            @PathVariable String employeeName,
            Model model
    ) {
        EmployeDTO employe = employeService.getEmployeByName(employeeName);
        List<SalarySlipDTO> salarySlips = salarySlipService.getSalarySlipsByEmployee(employeeName);
        
        model.addAttribute("employe", employe);
        model.addAttribute("salarySlips", salarySlips);
        return "employe/salaries";
    }

    @GetMapping("/salaryslip/pdf")
    @RequireErpAuth
    public ResponseEntity<byte[]> viewSalarySlipPdf(
            @RequestParam String name
    ) throws DocumentException {
        SalarySlipDTO salarySlip = salarySlipService.getSalarySlipByName(name);
        EmployeDTO employe = employeService.getEmployeByName(salarySlip.getEmployee());
        byte[] pdfBytes = pdfService.generatePdf(salarySlip, employe);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "fiche_paie_" + salarySlip.getEmployee() + "_" + salarySlip.getStartDate() + "_" + salarySlip.getEndDate() + ".pdf";
        
        // Modification clé ici: "inline" au lieu de "attachment"
        headers.setContentDispositionFormData("inline", filename);
        
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping
    @RequireErpAuth
    public String listSalarySlips(
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year,
            Model model) {
        
        // Récupérer l'année courante pour le filtre
        int currentYear = Year.now().getValue();
        model.addAttribute("currentYear", currentYear);
        
        // Construire les filtres
        Map<String, String> filters = new HashMap<>();
        if (month != null) filters.put("month", month.toString());
        if (year != null) filters.put("year", year.toString());
        
        // Récupérer les bulletins
        List<SalarySlipDTO> salarySlips = salarySlipService.findSalarySlipsByMonth(filters);
        model.addAttribute("salarySlips", salarySlips);
        
        return "salary/salaries";
    }    
}
