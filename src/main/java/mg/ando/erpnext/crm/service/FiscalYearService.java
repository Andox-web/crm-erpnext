package mg.ando.erpnext.crm.service;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.FiscalYearDTO;

import java.util.List;

public interface FiscalYearService {
    FiscalYearDTO getByName(String name);
    List<FiscalYearDTO> getAll();
    FiscalYearDTO create(FiscalYearDTO dto);
    List<FiscalYearDTO> createAll(List<FiscalYearDTO> dtos);
    void delete(String name);
    int deleteAll(List<String> names);
    List<FiscalYearDTO> getWithFilters(List<Filter> filters);
}