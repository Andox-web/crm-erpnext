package mg.ando.erpnext.crm.service.salary;

import java.util.List;

import mg.ando.erpnext.crm.dto.SalarySlipDTO;

public interface SalarySlipConditionService {
    void applyConditions(Double base,int sign,
                         List<String> composants, List<String> operateurs, List<Double> valeurs);
    boolean satisfiesCondition(SalarySlipDTO slip, 
                                      String composant, 
                                      String operateur, 
                                      Double valeur);
}
