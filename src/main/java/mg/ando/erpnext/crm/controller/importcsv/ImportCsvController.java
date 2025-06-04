package mg.ando.erpnext.crm.controller.importcsv;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.service.importcsv.ImportCsvService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/import")
public class ImportCsvController {

    private final ImportCsvService importCsvService;

    public ImportCsvController(ImportCsvService importCsvService) {
        this.importCsvService = importCsvService;
    }

    @PostMapping("/csv")
    @RequireErpAuth
    public String importCsvFiles(
            @RequestParam("employeCsv") MultipartFile employeCsv,
            @RequestParam("salaryStructureCsv") MultipartFile salaryStructureCsv,
            @RequestParam("salarySlipCsv") MultipartFile salarySlipCsv,
            RedirectAttributes redirectAttributes) {

        try {
            importCsvService.importCsv(employeCsv, salaryStructureCsv, salarySlipCsv);
            redirectAttributes.addFlashAttribute("success", "Importation réussie !");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erreur d'importation: " + e.getMessage());
        }
        
        return "redirect:/import";
    }
    
    @GetMapping
    @RequireErpAuth
    public String importPage(){
        return "importCsv";
    }
}