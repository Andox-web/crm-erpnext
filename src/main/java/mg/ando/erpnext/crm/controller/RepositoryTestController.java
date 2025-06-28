package mg.ando.erpnext.crm.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.ando.erpnext.crm.dto.CompanyDTO;
import mg.ando.erpnext.crm.dto.DepartementDTO;
import mg.ando.erpnext.crm.dto.EmployeDTO;
import mg.ando.erpnext.crm.dto.GenderDTO;
import mg.ando.erpnext.crm.dto.SalaryAssignmentDTO;
import mg.ando.erpnext.crm.dto.SalaryComponentDTO;
import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.dto.SalaryStructureDTO;
import mg.ando.erpnext.crm.repository.CompanyRepository;
import mg.ando.erpnext.crm.repository.DepartementRepository;
import mg.ando.erpnext.crm.repository.EmployeRepository;
import mg.ando.erpnext.crm.repository.GenderRepository;
import mg.ando.erpnext.crm.repository.SalaryAssignmentRepository;
import mg.ando.erpnext.crm.repository.SalaryComponentRepository;
import mg.ando.erpnext.crm.repository.SalaryDetailRepository;
import mg.ando.erpnext.crm.repository.SalarySlipRepository;
import mg.ando.erpnext.crm.repository.SalaryStructureRepository;

@RestController
@RequestMapping("/api/test-repositories")
public class RepositoryTestController {

    private final EmployeRepository employeRepository;
    private final GenderRepository genderRepository;
    private final CompanyRepository companyRepository;
    private final SalaryAssignmentRepository salaryAssignmentRepository;
    private final SalaryComponentRepository salaryComponentRepository;
    private final SalaryDetailRepository salaryDetailRepository;
    private final SalarySlipRepository salarySlipRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final DepartementRepository departementRepository;

    public RepositoryTestController(
            EmployeRepository employeRepository,
            GenderRepository genderRepository,
            CompanyRepository companyRepository,
            SalaryAssignmentRepository salaryAssignmentRepository,
            SalaryComponentRepository salaryComponentRepository,
            SalaryDetailRepository salaryDetailRepository,
            SalarySlipRepository salarySlipRepository,
            SalaryStructureRepository salaryStructureRepository,
            DepartementRepository departementRepository) {
        this.employeRepository = employeRepository;
        this.genderRepository = genderRepository;
        this.companyRepository = companyRepository;
        this.salaryAssignmentRepository = salaryAssignmentRepository;
        this.salaryComponentRepository = salaryComponentRepository;
        this.salaryDetailRepository = salaryDetailRepository;
        this.salarySlipRepository = salarySlipRepository;
        this.salaryStructureRepository = salaryStructureRepository;
        this.departementRepository = departementRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> testAllRepositories() {
        Map<String, Object> response = new HashMap<>();

        // Tester chaque repository
        response.put("employes", employeRepository.findAll());
        response.put("genders", genderRepository.findAll());
        response.put("companies", companyRepository.findAll());
        response.put("salaryAssignments", salaryAssignmentRepository.findAll());
        response.put("salaryComponents", salaryComponentRepository.findAll());
        response.put("salaryDetails", salaryDetailRepository.findAll());
        response.put("salarySlips", salarySlipRepository.findAll());
        response.put("salaryStructures", salaryStructureRepository.findAll());
        response.put("departements", departementRepository.findAll());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/insert")
    public ResponseEntity<Map<String, Object>> testInsertOperations() {
        Map<String, Object> response = new HashMap<>();
        String suffix = String.valueOf(System.currentTimeMillis());
        
        try {
            // 1. Créer une entreprise
            CompanyDTO company = new CompanyDTO();
            company.setName("TestCompany_" + suffix);
            company.setCompanyName("Test Company " + suffix);
            company.setDefaultCurrency("USD");
            CompanyDTO savedCompany = companyRepository.save(company);
            response.put("company", savedCompany);

            // 2. Créer un département
            DepartementDTO department = new DepartementDTO();
            department.setName("TestDept_" + suffix);
            department.setDepartmentName("Test Department " + suffix);
            department.setCompany(savedCompany.getName());
            department.setGroup(true);
            DepartementDTO savedDepartment = departementRepository.save(department);
            response.put("department", savedDepartment);

            // 3. Créer un genre
            GenderDTO gender = new GenderDTO();
            gender.setName("TestGender_" + suffix);
            gender.setGender("Test Gender " + suffix);
            GenderDTO savedGender = genderRepository.save(gender);
            response.put("gender", savedGender);

            // 4. Créer un employé
            EmployeDTO employee = new EmployeDTO();
            employee.setName("TestEmp_" + suffix);
            employee.setEmployeeName("Test Employee " + suffix);
            employee.setCompany(savedCompany.getName());
            employee.setDepartment(savedDepartment.getName());
            employee.setGender(savedGender.getGender());
            employee.setDateOfJoining(LocalDate.now().toString());
            employee.setStatus("Active");
            EmployeDTO savedEmployee = employeRepository.save(employee);
            response.put("employee", savedEmployee);

            // 5. Créer un composant salarial
            SalaryComponentDTO component = new SalaryComponentDTO();
            component.setName("TestComp_" + suffix);
            component.setSalaryComponent("Test Component " + suffix);
            component.setAbbr("TC" + suffix);
            component.setType("Earning");
            component.setAmountBasedOnFormula(false);
            SalaryComponentDTO savedComponent = salaryComponentRepository.save(component);
            response.put("salaryComponent", savedComponent);

            // 6. Créer une structure salariale
            SalaryStructureDTO structure = new SalaryStructureDTO();
            structure.setName("TestStruct_" + suffix);
            structure.setCompany(savedCompany.getName());
            structure.setIsActive("Yes");
            structure.setPayrollFrequency("Monthly");
            SalaryStructureDTO savedStructure = salaryStructureRepository.save(structure);
            response.put("salaryStructure", savedStructure);

            // 7. Créer une assignation salariale
            SalaryAssignmentDTO assignment = new SalaryAssignmentDTO();
            assignment.setName("TestAssign_" + suffix);
            assignment.setEmployee(savedEmployee.getName());
            assignment.setEmployeeName(savedEmployee.getEmployeeName());
            assignment.setCompany(savedCompany.getName());
            assignment.setSalaryStructure(savedStructure.getName());
            assignment.setBase(5000.0);
            assignment.setVariable(1000.0);
            assignment.setFromDate(LocalDate.now().toString());
            assignment.setToDate(LocalDate.now().plusYears(1).toString());
            assignment.setCurrency("USD");
            SalaryAssignmentDTO savedAssignment = salaryAssignmentRepository.save(assignment);
            response.put("salaryAssignment", savedAssignment);

            // 8. Créer une fiche de paie
            SalarySlipDTO slip = new SalarySlipDTO();
            slip.setName("TestSlip_" + suffix);
            slip.setEmployee(savedEmployee.getName());
            slip.setEmployeeName(savedEmployee.getEmployeeName());
            slip.setStartDate(LocalDate.now().toString());
            slip.setEndDate(LocalDate.now().plusMonths(1).toString());
            slip.setCompany(savedCompany.getName());
            slip.setCurrency("USD");
            slip.setGrossPay(6000.0);
            slip.setRoundedTotal(6000.0);
            slip.setNetPay(4800.0);
            slip.setStatus("Draft");
            slip.setSalaryStructure(savedStructure.getName());
            slip.setTotalDeduction(1200.0);
            SalarySlipDTO savedSlip = salarySlipRepository.save(slip);
            response.put("salarySlip", savedSlip);

            // 9. Créer des détails salariaux
            SalaryDetailDTO detail = new SalaryDetailDTO();
            detail.setName("TestDetail_" + suffix);
            detail.setParent(savedSlip.getName());
            detail.setParentfield("earnings");
            detail.setSalaryComponent(savedComponent.getName());
            detail.setAmount(5000.0);
            detail.setType("Earning");
            SalaryDetailDTO savedDetail = salaryDetailRepository.save(detail);
            response.put("salaryDetail", savedDetail);

            response.put("status", "success");
            response.put("message", "All test records inserted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Insertion failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}