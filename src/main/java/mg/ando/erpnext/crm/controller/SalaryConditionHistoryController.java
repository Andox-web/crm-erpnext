package mg.ando.erpnext.crm.controller;

import mg.ando.erpnext.crm.dto.SalaryConditionHistoryDTO;
import mg.ando.erpnext.crm.repository.SalaryConditionHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/salary-condition-history")
public class SalaryConditionHistoryController {

    private final SalaryConditionHistoryRepository historyRepository;

    public SalaryConditionHistoryController(SalaryConditionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    // Endpoint pour créer un historique de test
    @GetMapping("/test")
    public ResponseEntity<SalaryConditionHistoryDTO> createTestHistory() {
        SalaryConditionHistoryDTO history = new SalaryConditionHistoryDTO();
        
        // Création d'un enregistrement de test
        history.setOperationDate(LocalDateTime.now());
        history.setBase(10.0);
        history.setSign(1);
        history.setStatus("SUCCESS");
        
        // Ajout de conditions de test
        SalaryConditionHistoryDTO.ConditionDTO condition1 = new SalaryConditionHistoryDTO.ConditionDTO();
        condition1.setComponent("Basic Salary");
        condition1.setOperator(">");
        condition1.setValue(1000.0);
        
        SalaryConditionHistoryDTO.ConditionDTO condition2 = new SalaryConditionHistoryDTO.ConditionDTO();
        condition2.setComponent("Transport Allowance");
        condition2.setOperator("<=");
        condition2.setValue(200.0);
        
        history.setConditions(List.of(condition1, condition2));
        history.setAffectedSalarySlips(List.of("SLIP-001", "SLIP-002"));
        
        SalaryConditionHistoryDTO savedHistory = historyRepository.save(history);
        return ResponseEntity.ok(savedHistory);
    }

    // Endpoint pour récupérer tous les historiques
    @GetMapping
    public ResponseEntity<List<SalaryConditionHistoryDTO>> getAllHistory() {
        List<SalaryConditionHistoryDTO> allHistory = historyRepository.findAll();
        return ResponseEntity.ok(allHistory);
    }
}