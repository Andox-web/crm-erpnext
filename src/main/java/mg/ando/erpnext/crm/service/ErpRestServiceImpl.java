package mg.ando.erpnext.crm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// ErpRestService.java
@Service
public class ErpRestServiceImpl implements ErpRestService{

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public ErpRestServiceImpl(RestTemplate restTemplate,@Value("${erp.base-url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    // Méthode générique qui retourne l'objet désérialisé
    public <T> T callErpApi(String endpoint, HttpMethod method, Object requestBody,HttpHeaders httpHeaders ,Class<T> responseType) {
        ResponseEntity<T> response = callErpApiWithResponse(endpoint, method, requestBody, httpHeaders, responseType);
        return response.getBody();
    }

    // Nouvelle méthode qui retourne le ResponseEntity complet
    public <T> ResponseEntity<T> callErpApiWithResponse(
            String endpoint, 
            HttpMethod method, 
            Object requestBody, 
            HttpHeaders httpHeaders,
            Class<T> responseType) {
        
        String url = apiUrl + endpoint;
        HttpEntity<Object> requestEntity = requestBody != null 
                ? new HttpEntity<>(requestBody,httpHeaders)
                : new HttpEntity<>(httpHeaders);
        
        return restTemplate.exchange(
            url,
            method,
            requestEntity,
            responseType
        );
    }
}
