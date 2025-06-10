package mg.ando.erpnext.crm.controller;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.dto.MonthSalaryDetail;
import mg.ando.erpnext.crm.dto.MonthSalarySlip;
import mg.ando.erpnext.crm.service.salary.SalarySlipService;

@Controller
@RequestMapping("/dashboard")
@RequireErpAuth
public class DashboardController {

    private final SalarySlipService salaryService;
    public DashboardController(SalarySlipService salaryService) {
        this.salaryService = salaryService;
    }
    @GetMapping
    public String dashboard(){
        return "dashboard";
    }

    @GetMapping("/{year}")
    @ResponseBody
    public ResponseEntity<?> getSalarySlipsByYear(
            @PathVariable("year") String year) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("month_salary_slip", salaryService.groupSalarySlipByMonthForYear(year));
            data.put("month_salary_detail", salaryService.groupSalaryDetailByMonthForYear(year));
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/salary-slip/{year}")
    @ResponseBody
    public ResponseEntity<?> getSalarySlipByYear(
            @PathVariable("year") String year) {
        try {
            Map<YearMonth, MonthSalarySlip> salarySlips = salaryService.groupSalarySlipByMonthForYear(year);
            return ResponseEntity.ok(salarySlips);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/salary-detail/{year}")
    @ResponseBody
    public ResponseEntity<?> getSalaryDetailByYear(
            @PathVariable("year") String year) {
        try {
            Map<String, Map<YearMonth, MonthSalaryDetail>> salaryDetails = salaryService.groupSalaryDetailByMonthForYear(year);
            return ResponseEntity.ok(salaryDetails);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/salary-detail/{yearMonth}/one")
    @ResponseBody
    public ResponseEntity<?> getSalarySlipByMonth(
            @PathVariable("yearMonth") String month) {
        try {
            YearMonth yearMonth = YearMonth.parse(month);
            List<MonthSalaryDetail> salaryDetails = salaryService.groupSalaryDetailByMonth(yearMonth);
            return ResponseEntity.ok(salaryDetails);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/salary-slip/{year}/{month}")
    @ResponseBody
    public ResponseEntity<?> getSalarySlipSummaryByMonth(
            @PathVariable("year") String year,
            @PathVariable("month") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) YearMonth month) {
        try {
            MonthSalarySlip salarySlipSummary = salaryService.groupSalarySlipByMonth(month);
            return ResponseEntity.ok(salarySlipSummary);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
