package mg.ando.erpnext.crm.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import mg.ando.erpnext.crm.config.Filter;

// ErpRestService.java
@Service
public class ErpRestServiceImpl implements ErpRestService{

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    public ErpRestServiceImpl(RestTemplate restTemplate,@Value("${erp.base-url}") String apiUrl, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.objectMapper = objectMapper;
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
        // Ajout du paramètre limit_page_length=1000
        if (url.contains("?")) {
            url += "&limit_page_length=10000";
        } else {
            url += "?limit_page_length=10000";
        }
        
        HttpEntity<Object> requestEntity = requestBody != null
                ? new HttpEntity<>(requestBody, httpHeaders)
                : new HttpEntity<>(httpHeaders);

        if (responseType.equals(String.class)) {
            ResponseEntity<String> stringResponse = restTemplate.exchange(
                url,
                method,
                requestEntity,
                String.class
            );
            return (ResponseEntity<T>) ResponseEntity
                .status(stringResponse.getStatusCode())
                .headers(stringResponse.getHeaders())
                .body((T) stringResponse.getBody());
        }

        // 1. Désérialisation dans un type générique
        ResponseEntity<Map<String, Object>> rawResponse = restTemplate.exchange(
            url,
            method,
            requestEntity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> responseBody = rawResponse.getBody();
        
        // 2. Gestion du cas où 'data' est absent
        if (responseBody == null || !responseBody.containsKey("data")) {
            // Aucun traitement spécial - retourne la réponse complète
            // selon le type demandé par l'appelant
            ObjectMapper mapper = new ObjectMapper();
            T fullResponse = mapper.convertValue(responseBody, responseType);
            
            return new ResponseEntity<>(
                fullResponse,
                rawResponse.getHeaders(),
                rawResponse.getStatusCode()
            );
        }

        // 3. Extraction et conversion du champ 'data'
        Object data = responseBody.get("data");
        T result = objectMapper.convertValue(data, objectMapper.getTypeFactory().constructType(responseType));

        return new ResponseEntity<>(
            result,
            rawResponse.getHeaders(),
            rawResponse.getStatusCode()
        );
    }
    public <T> T callErpApiWithFieldAndFilter(
            String endpoint, 
            HttpMethod method, 
            Object requestBody,
            HttpHeaders httpHeaders, 
            String[] fields, 
            List<Filter> filters,
            Class<T> responseType) {

        // Construire l'URL avec les paramètres
        StringBuilder urlBuilder = new StringBuilder(endpoint);
        
        // Ajouter les paramètres s'ils existent
        if (fields != null || filters != null) {
            urlBuilder.append("?");
        }
        
        // Ajouter les champs
        if (fields != null && fields.length > 0) {
            urlBuilder.append("fields=[");
            for (int i = 0; i < fields.length; i++) {
                urlBuilder.append("\"").append(fields[i]).append("\"");
                if (i < fields.length - 1) {
                    urlBuilder.append(",");
                }
            }
            urlBuilder.append("]");
        }
        
        // Ajouter les filtres
        if (filters != null && !filters.isEmpty()) {
            // Ajouter un séparateur si des champs sont déjà présents
            if (fields != null && fields.length > 0) {
                urlBuilder.append("&");
            }
            
            urlBuilder.append("filters=[");
            int count = 0;
            for (Filter entry : filters) {
                urlBuilder.append(entry.toJson());
                
                if (count < filters.size() - 1) {
                    urlBuilder.append(",");
                }
                count++;
            }
            urlBuilder.append("]");
        }
        
        return callErpApi(urlBuilder.toString(), method, requestBody, httpHeaders, responseType);
    }
}