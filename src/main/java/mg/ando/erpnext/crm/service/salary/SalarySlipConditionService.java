package mg.ando.erpnext.crm.service.salary;

import java.util.List;

public interface SalarySlipConditionService {
    void applyConditions(List<String> employes, Double base,
                         List<String> composants, List<String> operateurs, List<Double> valeurs);
}
