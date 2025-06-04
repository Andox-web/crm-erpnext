package mg.ando.erpnext.crm.service.employe;

import mg.ando.erpnext.crm.dto.CompanyDTO;

import java.util.List;

public interface CompanyService {
    CompanyDTO getCompanyByName(String name);
    List<CompanyDTO> getAllCompanies();
    void createCompany(CompanyDTO companyDTO);
    int createAllCompanies(List<CompanyDTO> companyDTOs);
    void deleteCompany(String name);
    int deleteAllCompanies(List<String> companyNames);
}