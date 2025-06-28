package mg.ando.erpnext.crm.service.employe;

import mg.ando.erpnext.crm.config.Filter;
import mg.ando.erpnext.crm.dto.CompanyDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO getCompanyByName(String name);
    CompanyDTO getByName(String name);
    List<CompanyDTO> getAllCompanies();
    CompanyDTO createCompany(CompanyDTO companyDTO);
    List<CompanyDTO> createAllCompanies(List<CompanyDTO> companyDTOs);
    void deleteCompany(String name);
    int deleteAllCompanies(List<String> companyNames);
    List<CompanyDTO> getWithFilters(List<Filter> filters);
}