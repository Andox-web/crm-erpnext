package mg.ando.erpnext.crm.service.salary;

import com.lowagie.text.DocumentException;

import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;

public interface PdfService {
    byte[] generatePdf(SalarySlipDTO salarySlip, EmployeDTO employee) throws DocumentException;
}
