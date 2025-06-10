package mg.ando.erpnext.crm.controller.importcsv;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.service.importcsv.ImportCsvService;

@Controller
@RequestMapping("/import")
public class ImportCsvController {

    private final ImportCsvService importCsvService;

    public ImportCsvController(ImportCsvService importCsvService) {
        this.importCsvService = importCsvService;
    }

    @PostMapping("/csv")
    @RequireErpAuth
    @ResponseBody
    public ResponseEntity<String> importCsvFiles(
            @RequestParam("employeCsv") MultipartFile employeCsv,
            @RequestParam("salaryStructureCsv") MultipartFile salaryStructureCsv,
            @RequestParam("salarySlipCsv") MultipartFile salarySlipCsv) {

        try {
            importCsvService.importCsv(employeCsv, salaryStructureCsv, salarySlipCsv);
            return ResponseEntity.ok("{\"success\": true, \"message\": \"CSV files imported successfully.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            String safeMessage = e.getMessage();
            if (safeMessage != null) {
                safeMessage = safeMessage.replace("\"", "'").replace("\n", " ");
            } else {
                safeMessage = "Unknown error";
            }
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + safeMessage + "\"}");
        }
    }
    
    @GetMapping
    @RequireErpAuth
    public String importPage(){
        return "importCsv";
    }
}