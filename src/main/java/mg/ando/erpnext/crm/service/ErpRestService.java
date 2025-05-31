package mg.ando.erpnext.crm.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface ErpRestService {
    public <T> ResponseEntity<T> callErpApiWithResponse(
            String endpoint, 
            HttpMethod method, 
            Object requestBody, 
            HttpHeaders httpHeaders,
            Class<T> responseType);
    public <T> T callErpApi(String endpoint, HttpMethod method, Object requestBody,HttpHeaders httpHeaders, Class<T> responseType);
}
