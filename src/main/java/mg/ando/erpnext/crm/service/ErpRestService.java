package mg.ando.erpnext.crm.service;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import mg.ando.erpnext.crm.config.Filter;

public interface ErpRestService {
    public <T> ResponseEntity<T> callErpApiWithResponse(
            String endpoint, 
            HttpMethod method, 
            Object requestBody, 
            HttpHeaders httpHeaders,
            Class<T> responseType);
    public <T> T callErpApi(String endpoint, HttpMethod method, Object requestBody,HttpHeaders httpHeaders, Class<T> responseType);
    public <T> T callErpApiWithFieldAndFilter(String endpoint, HttpMethod method, Object requestBody,HttpHeaders httpHeaders ,String[] fields,List<Filter> filters,Class<T> responseType);
}
