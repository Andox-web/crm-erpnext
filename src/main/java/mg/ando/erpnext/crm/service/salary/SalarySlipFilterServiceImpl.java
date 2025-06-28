package mg.ando.erpnext.crm.service.salary;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import mg.ando.erpnext.crm.dto.SalarySlipDTO;

@Service
public class SalarySlipFilterServiceImpl implements SalarySlipFilterService{

    SalarySlipService salarySlipService;
    SalarySlipConditionService salarySlipConditionService;
    

    public SalarySlipFilterServiceImpl(SalarySlipService salarySlipService,
            SalarySlipConditionService salarySlipConditionService) {
        this.salarySlipService = salarySlipService;
        this.salarySlipConditionService = salarySlipConditionService;
    }


    @Override
    public List<SalarySlipDTO> getSalarySlipFilter(String composant, String operateur, Double valeur) {
        List<SalarySlipDTO> salarySlipDTOs = salarySlipService.getAllSalarySlips();
        List<SalarySlipDTO> result=new LinkedList<>();
        for (SalarySlipDTO salarySlipDTO : salarySlipDTOs) {
            if (salarySlipConditionService.satisfiesCondition(salarySlipService.getByName(salarySlipDTO.getName()), composant, operateur, valeur)) {
                result.add(salarySlipDTO);
            }
        }
        return result;
    }
    
}
