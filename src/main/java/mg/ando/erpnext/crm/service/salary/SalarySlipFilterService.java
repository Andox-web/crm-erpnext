package mg.ando.erpnext.crm.service.salary;

import java.util.List;

import mg.ando.erpnext.crm.dto.SalarySlipDTO;

public interface SalarySlipFilterService {
    List<SalarySlipDTO> getSalarySlipFilter(String composant,String operateur,Double valeur);    
}
