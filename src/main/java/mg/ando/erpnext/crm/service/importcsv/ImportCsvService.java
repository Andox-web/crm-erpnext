package mg.ando.erpnext.crm.service.importcsv;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ImportCsvService {
    void importCsv(MultipartFile employeCsv,MultipartFile salaryStructureCsv,MultipartFile salarySlipCsv) throws IOException;
}
