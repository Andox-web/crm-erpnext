package mg.ando.erpnext.crm.service.salary;

import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.MonthSalaryDetail;
import mg.ando.erpnext.crm.dto.MonthSalarySlip;
import mg.ando.erpnext.crm.dto.SalaryDetailDTO;
import mg.ando.erpnext.crm.dto.SalarySlipDTO;
import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {

    private final ErpRestService erpRestService;
    private static final String SALARY_SLIP_ENDPOINT = "/api/resource/Salary Slip";

    // Champs par défaut pour les Salary Slips
    private static final String[] DEFAULT_FIELDS = {
        "name",
        "employee",
        "employee_name",
        "start_date",
        "end_date",
        "posting_date",
        "company",
        "currency",
        "gross_pay",
        "net_pay",
        "status",
        "total_deduction",
        "salary_structure",
        "rounded_total"
    };

    public SalarySlipServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    @Override
    public SalarySlipDTO getSalarySlipByName(String name) {
        String endpoint = SALARY_SLIP_ENDPOINT + "/" + name;
        return erpRestService.callErpApi(
            endpoint,
            HttpMethod.GET,
            null,
            null,
            SalarySlipDTO.class
        );
    }
    @Override
    public List<SalarySlipDTO> getSalarySlipsByEmployee(String employeeName) {
        HttpHeaders headers = null;

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("employee", "=", employeeName));

        SalarySlipDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.GET,
            null,
            headers,
            DEFAULT_FIELDS,
            filters,
            SalarySlipDTO[].class
        );

        return result != null ? List.of(result) : Collections.emptyList();
    }
    @Override
    public List<SalarySlipDTO> getAllSalarySlips() {
        SalarySlipDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            null,
            SalarySlipDTO[].class
        );
        
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public void createSalarySlip(SalarySlipDTO salarySlipDTO) {
        erpRestService.callErpApi(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.POST,
            salarySlipDTO,
            null,
            Object.class
        );
    }

    @Override
    public int createAllSalarySlips(List<SalarySlipDTO> salarySlipDTOs) {
        if (salarySlipDTOs == null || salarySlipDTOs.isEmpty()) {
            return 0;
        }

        List<Map<String, Object>> docs = new ArrayList<>();
        for (SalarySlipDTO dto : salarySlipDTOs) {
            Map<String, Object> slipMap = new HashMap<>();
            slipMap.put("doctype", "Salary Slip");
            slipMap.put("name", dto.getName());
            slipMap.put("employee", dto.getEmployee());
            slipMap.put("employee_name", dto.getEmployeeName());
            slipMap.put("start_date", dto.getStartDate());
            slipMap.put("company", dto.getCompany());
            slipMap.put("salary_structure", dto.getSalaryStructure());
            docs.add(slipMap);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("docs", docs);

        try {
            Map<String, Object> response = erpRestService.callErpApi(
                "/api/method/frappe.client.insert_many",
                HttpMethod.POST,
                requestBody,
                null,
                Map.class
            );

            if (response != null && response.containsKey("message")) {
                List<?> resultList = (List<?>) response.get("message");
                return resultList.size();
            }
        } catch (Exception e) {
            System.err.println("Erreur création en masse des bulletins : " + e.getMessage());
        }

        throw new RuntimeException("Erreur lors de la création en masse des bulletins");
    }

    @Override
    public void deleteSalarySlip(String name) {
        String endpoint = SALARY_SLIP_ENDPOINT + "/" + name;
        erpRestService.callErpApi(
            endpoint,
            HttpMethod.DELETE,
            null,
            null,
            Object.class
        );
    }

    @Override
    public int deleteAllSalarySlips(List<String> salarySlipNames) {
        if (salarySlipNames == null || salarySlipNames.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (String name : salarySlipNames) {
            try {
                deleteSalarySlip(name);
                count++;
            } catch (Exception e) {
                throw new RuntimeException("Erreur suppression bulletin " + name, e);
            }
        }
        return count;
    }

    @Override
    public List<SalarySlipDTO> findSalarySlipsByMonth(Map<String, String> filtre) {
        List<Filter> filters = new ArrayList<>();
        
        // Gestion du filtre année seule
        if (filtre.containsKey("year") && !filtre.get("year").isEmpty()) {
            int year = Integer.parseInt(filtre.get("year"));
            
            if (filtre.containsKey("month") && !filtre.get("month").isEmpty()) {
                // Filtre mois + année
                int month = Integer.parseInt(filtre.get("month"));
                YearMonth yearMonth = YearMonth.of(year, month);
                filters.add(new Filter("start_date", ">=", yearMonth.atDay(1).toString()));
                filters.add(new Filter("end_date", "<=", yearMonth.atEndOfMonth().toString()));
            } else {
                // Filtre année seule
                filters.add(new Filter("start_date", ">=", Year.of(year).atDay(1).toString()));
                filters.add(new Filter("end_date", "<=", Year.of(year).atMonth(12).atEndOfMonth().toString()));
            }
        }
        // Si seulemement le mois est fourni sans année, on utilise l'année courante
        else if (filtre.containsKey("month") && !filtre.get("month").isEmpty()) {
            int month = Integer.parseInt(filtre.get("month"));
            int currentYear = Year.now().getValue();
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            filters.add(new Filter("start_date", ">=", yearMonth.atDay(1).toString()));
            filters.add(new Filter("end_date", "<=", yearMonth.atEndOfMonth().toString()));
        }
        
        SalarySlipDTO[] result = erpRestService.callErpApiWithFieldAndFilter(
            SALARY_SLIP_ENDPOINT,
            HttpMethod.GET,
            null,
            null,
            DEFAULT_FIELDS,
            filters,
            SalarySlipDTO[].class
        );
        System.out.println(result.length);
        return result != null ? Arrays.asList(result) : Collections.emptyList();
    }

    @Override
    public  Map<YearMonth,MonthSalarySlip> groupSalarySlipByMonthForYear(String year) {
        List<SalarySlipDTO> salarySlips = findSalarySlipsByMonth(Map.of("year",year));
        YearMonth baseYearMonth = (year == null || year.isEmpty()) ? YearMonth.now().minusMonths(12) : YearMonth.of(Integer.parseInt(year), 1);
        Map<YearMonth, MonthSalarySlip> groupedSlips = new HashMap<>();
        for (int i = 0 ; i < 12 ; i++ ){
            YearMonth yearMonth = baseYearMonth.plusMonths(i);
            MonthSalarySlip monthSalarySlip = new MonthSalarySlip();
            monthSalarySlip.setYearMonth(yearMonth);
            monthSalarySlip.setStartDate(yearMonth.atDay(1).toString());
            monthSalarySlip.setEndDate(yearMonth.atEndOfMonth().toString());
            monthSalarySlip.setGrossPay(0.0);
            monthSalarySlip.setNetPay(0.0);
            monthSalarySlip.setTotalDeduction(0.0);
            monthSalarySlip.setRoundedTotal(0.0);
            groupedSlips.put(yearMonth, monthSalarySlip);

            for (SalarySlipDTO slip : salarySlips) {
                YearMonth slipYearMonth = YearMonth.parse(slip.getStartDate().substring(0, 7));
                if (slipYearMonth.equals(yearMonth)) {
                    monthSalarySlip.setGrossPay(monthSalarySlip.getGrossPay() + slip.getGrossPay());
                    monthSalarySlip.setNetPay(monthSalarySlip.getNetPay() + slip.getNetPay());
                    monthSalarySlip.setTotalDeduction(monthSalarySlip.getTotalDeduction() + slip.getTotalDeduction());
                    monthSalarySlip.setRoundedTotal(monthSalarySlip.getRoundedTotal() + slip.getRoundedTotal());
                }
            }
        } 
        return groupedSlips;
    }

    @Override
    public Map<String, Map<YearMonth, MonthSalaryDetail>> groupSalaryDetailByMonthForYear(String year) {
        Map<String, String> filter = new HashMap<>();
        filter.put("year", year);

        List<SalarySlipDTO> salarySlips =  findSalarySlipsByMonth(filter);

        Map<String, Map<YearMonth, MonthSalaryDetail>> result = new HashMap<>();

        // Créer la liste de tous les mois de l'année
        int yearInt = Integer.parseInt(year);
        List<YearMonth> allMonths = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            allMonths.add(YearMonth.of(yearInt, month));
        }

        for (SalarySlipDTO salarySlipRaw : salarySlips) {
            SalarySlipDTO salarySlip = getSalarySlipByName(salarySlipRaw.getName());
            YearMonth yearMonth = YearMonth.parse(salarySlip.getStartDate().substring(0, 7));
            for (SalaryDetailDTO detail : salarySlip.getEarnings()) {
                String key = detail.getSalaryComponent()+"|Earnings";
                Map<YearMonth, MonthSalaryDetail> mapMonth = result.computeIfAbsent(key, k -> new HashMap<>());
                
                // Initialiser tous les mois pour cette clé
                for (YearMonth month : allMonths) {
                    mapMonth.putIfAbsent(month, createEmptyDetail(month, detail.getSalaryComponent(), "Earnings"));
                }
                
                // Mettre à jour le mois courant
                MonthSalaryDetail monthSalaryDetail = mapMonth.get(yearMonth);
                monthSalaryDetail.setAmount(monthSalaryDetail.getAmount() + (detail.getAmount() != null ? detail.getAmount() : 0.0));
            }
            for (SalaryDetailDTO detail : salarySlip.getDeductions()) {
                String key = detail.getSalaryComponent()+"|Deductions";
                Map<YearMonth, MonthSalaryDetail> mapMonth = result.computeIfAbsent(key, k -> new HashMap<>());
                
                // Initialiser tous les mois pour cette clé
                for (YearMonth month : allMonths) {
                    mapMonth.putIfAbsent(month, createEmptyDetail(month, detail.getSalaryComponent(), "Deductions"));
                }
                
                // Mettre à jour le mois courant
                MonthSalaryDetail monthSalaryDetail = mapMonth.get(yearMonth);
                monthSalaryDetail.setAmount(monthSalaryDetail.getAmount() + (detail.getAmount() != null ? detail.getAmount() : 0.0));
            }
        }

        // Compléter les clés qui n'ont aucune donnée
        for (Map<YearMonth, MonthSalaryDetail> mapMonth : result.values()) {
            for (YearMonth month : allMonths) {
                mapMonth.putIfAbsent(month, createEmptyDetail(month, "", ""));
            }
        }

        return result;
    }

    // Méthode utilitaire pour créer un détail vide
    private MonthSalaryDetail createEmptyDetail(YearMonth month, String component, String type) {
        MonthSalaryDetail detail = new MonthSalaryDetail();
        detail.setAmount(0.0);
        detail.setMonth(month);
        detail.setSalaryComponent(component);
        detail.setType(type);
        return detail;
    }
    @Override
    public MonthSalarySlip groupSalarySlipByMonth(YearMonth yearMonth) {
        // Créer le filtre pour le mois spécifié
        Map<String, String> filter = new HashMap<>();
        filter.put("year", String.valueOf(yearMonth.getYear()));
        filter.put("month", String.valueOf(yearMonth.getMonthValue()));
        
        // Récupérer les bulletins du mois
        List<SalarySlipDTO> slips = findSalarySlipsByMonth(filter);
        
        // Créer l'objet de résultat
        MonthSalarySlip result = new MonthSalarySlip()     ;
        result.setYearMonth(yearMonth);
        result.setStartDate(yearMonth.atDay(1).toString());
        result.setEndDate(yearMonth.atEndOfMonth().toString());
        
        // Calculer les totaux
        double grossPay = 0.0;
        double netPay = 0.0;
        double totalDeduction = 0.0;
        double roundedTotal = 0.0;
        
        for (SalarySlipDTO slip : slips) {
            grossPay += slip.getGrossPay() != null ? slip.getGrossPay() : 0.0;
            netPay += slip.getNetPay() != null ? slip.getNetPay() : 0.0;
            totalDeduction += slip.getTotalDeduction() != null ? slip.getTotalDeduction() : 0.0;
            roundedTotal += slip.getRoundedTotal() != null ? slip.getRoundedTotal() : 0.0;
        }
        
        result.setGrossPay(grossPay);
        result.setNetPay(netPay);
        result.setTotalDeduction(totalDeduction);
        result.setRoundedTotal(roundedTotal);
        
        return result;
    }

    @Override
    public List<MonthSalaryDetail> groupSalaryDetailByMonth(YearMonth yearMonth) {
        // Créer le filtre pour le mois spécifié
        Map<String, String> filter = new HashMap<>();
        filter.put("year", String.valueOf(yearMonth.getYear()));
        filter.put("month", String.valueOf(yearMonth.getMonthValue()));
        List<SalarySlipDTO> slips = findSalarySlipsByMonth(filter);
        Map<String, MonthSalaryDetail> detailMap = new HashMap<>();

        for (SalarySlipDTO slip : slips) {
            SalarySlipDTO fullSlip = getSalarySlipByName(slip.getName());
            YearMonth slipMonth = YearMonth.parse(fullSlip.getStartDate().substring(0, 7));
            if (!slipMonth.equals(yearMonth)) continue;

            if (fullSlip.getEarnings() != null) {
                for (SalaryDetailDTO earning : fullSlip.getEarnings()) {
                    String key = earning.getSalaryComponent() + "|Earnings";
                    MonthSalaryDetail detail = detailMap.get(key);
                    if (detail == null) {
                        detail = new MonthSalaryDetail();
                        detail.setAmount(earning.getAmount());
                        detail.setMonth(yearMonth);
                        detail.setSalaryComponent(earning.getSalaryComponent());
                        detail.setType("Earnings");
                        detailMap.put(key, detail);
                    } else {
                        detail.setAmount(detail.getAmount() + (earning.getAmount() != null ? earning.getAmount() : 0.0));
                    }
                }
            }
            if (fullSlip.getDeductions() != null) {
                for (SalaryDetailDTO deduction : fullSlip.getDeductions()) {
                    String key = deduction.getSalaryComponent() + "|Deductions";
                    MonthSalaryDetail detail = detailMap.get(key);
                    if (detail == null) {
                        detail = new MonthSalaryDetail();
                        detail.setAmount(deduction.getAmount());
                        detail.setMonth(yearMonth);
                        detail.setSalaryComponent(deduction.getSalaryComponent());
                        detail.setType("Deductions");
                        detailMap.put(key, detail);
                    } else {
                        detail.setAmount(detail.getAmount() + (deduction.getAmount() != null ? deduction.getAmount() : 0.0));
                    }
                }
            }
        }
        return new ArrayList<>(detailMap.values());
    }
}