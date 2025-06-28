package mg.ando.erpnext.crm.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import mg.ando.erpnext.crm.config.Filter;

@Service
public class ErpRestServiceImpl implements ErpRestService {

    // ========== CONSTANTES ==========
    private static final String DEFAULT_DATA_PATH = "data";

    // ========== CHAMPS ==========
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    // ========== CONSTRUCTEUR ==========
    public ErpRestServiceImpl(RestTemplate restTemplate,
                            @Value("${erp.base-url}") String apiUrl,
                            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.objectMapper = objectMapper;
    }

    // ========== METHODES PUBLIQUES ==========

    // ----- Méthodes Class<T> responseType -----
    @Override
    public <T> T callApi(String endpoint, HttpMethod method, Object body,
                        HttpHeaders headers, Class<T> responseType,
                        String... params) {
        return callApi(endpoint, method, body, headers, responseType, 
                      new ApiOptions.Builder().build(), params);
    }

    @Override
    public <T> T callApi(String endpoint, HttpMethod method, Object body,
                        HttpHeaders headers, Class<T> responseType,
                        ApiOptions options, String... params) {
        return handleApiCall(endpoint, method, body, headers,
                           objectMapper.constructType(responseType),
                           initOptions(options), params);
    }

    @Override
    public <T> T callApiWithFilters(String endpoint, HttpMethod method, Object body,
                                  HttpHeaders headers, String[] fields,
                                  List<Filter> filters, Class<T> responseType,
                                  String... params) {
        return callApiWithFilters(endpoint, method, body, headers, fields,
                                filters, responseType,
                                new ApiOptions.Builder().build(), params);
    }

    @Override
    public <T> T callApiWithFilters(String endpoint, HttpMethod method, Object body,
                                  HttpHeaders headers, String[] fields,
                                  List<Filter> filters, Class<T> responseType,
                                  ApiOptions options, String... params) {
        String fullUrl = buildFullUrl(endpoint, fields, filters, params);
        return callApi(fullUrl, method, body, headers, responseType, options);
    }

    // ----- Méthodes TypeReference<T> responseType -----
    @Override
    public <T> T callApi(String endpoint, HttpMethod method, Object body,
                        HttpHeaders headers, TypeReference<T> responseType,
                        String... params) {
        return callApi(endpoint, method, body, headers, responseType,
                      new ApiOptions.Builder().build(), params);
    }

    @Override
    public <T> T callApi(String endpoint, HttpMethod method, Object body,
                        HttpHeaders headers, TypeReference<T> responseType,
                        ApiOptions options, String... params) {
        return handleApiCall(endpoint, method, body, headers,
                           objectMapper.constructType(responseType),
                           initOptions(options), params);
    }

    @Override
    public <T> T callApiWithFilters(String endpoint, HttpMethod method, Object body,
                                  HttpHeaders headers, String[] fields,
                                  List<Filter> filters, TypeReference<T> responseType,
                                  String... params) {
        return callApiWithFilters(endpoint, method, body, headers, fields,
                                filters, responseType,
                                new ApiOptions.Builder().build(), params);
    }

    @Override
    public <T> T callApiWithFilters(String endpoint, HttpMethod method, Object body,
                                  HttpHeaders headers, String[] fields,
                                  List<Filter> filters, TypeReference<T> responseType,
                                  ApiOptions options, String... params) {
        String fullUrl = buildFullUrl(endpoint, fields, filters, params);
        return callApi(fullUrl, method, body, headers, responseType, options);
    }

    // ----- Méthodes ResponseEntity<T> -----
    @Override
    public <T> ResponseEntity<T> callApiWithResponse(
            String endpoint, HttpMethod method, Object body,
            HttpHeaders headers, Class<T> responseType,
            String... params) {
        return callApiWithResponse(endpoint, method, body, headers,
                responseType, new ApiOptions.Builder().build(), params);
    }

    @Override
    public <T> ResponseEntity<T> callApiWithResponse(
            String endpoint, HttpMethod method, Object body,
            HttpHeaders headers, Class<T> responseType,
            ApiOptions options, String... params) {
        return handleApiCallWithResponse(endpoint, method, body, headers,
                objectMapper.constructType(responseType),
                initOptions(options), params);
    }

    @Override
    public <T> ResponseEntity<T> callApiWithResponse(
            String endpoint, HttpMethod method, Object body,
            HttpHeaders headers, TypeReference<T> responseType,
            String... params) {
        return callApiWithResponse(endpoint, method, body, headers,
                responseType, new ApiOptions.Builder().build(), params);
    }

    @Override
    public <T> ResponseEntity<T> callApiWithResponse(
            String endpoint, HttpMethod method, Object body,
            HttpHeaders headers, TypeReference<T> responseType,
            ApiOptions options, String... params) {
        return handleApiCallWithResponse(endpoint, method, body, headers,
                objectMapper.constructType(responseType),
                initOptions(options), params);
    }

    // ========== METHODES PRIVEES ==========

    // ----- Méthodes principales de traitement -----
    private <T> T handleApiCall(String endpoint, HttpMethod method, Object body,
                              HttpHeaders headers, JavaType responseType,
                              ApiOptions options, String... params) {
        String url = buildUrl(endpoint, params);
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        if (responseType.getRawClass().equals(String.class)) {
            ResponseEntity<String> response = restTemplate.exchange(
                url, method, requestEntity, String.class
            );
            return (T) processStringResponse(response, options);
        }

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            url, method, requestEntity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return processResponse(response, responseType, options);
    }

    private <T> ResponseEntity<T> handleApiCallWithResponse(
            String endpoint, HttpMethod method, Object body,
            HttpHeaders headers, JavaType responseType,
            ApiOptions options, String... params) {
        String url = buildUrl(endpoint, params);
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        if (responseType.getRawClass().equals(String.class)) {
            ResponseEntity<String> response = restTemplate.exchange(
                url, method, requestEntity, String.class
            );
            String bodyContent = processStringResponse(response, options);
            return (ResponseEntity<T>) new ResponseEntity<>(
                bodyContent,
                response.getHeaders(),
                response.getStatusCode()
            );
        }

        ResponseEntity<Map<String, Object>> rawResponse = restTemplate.exchange(
                url, method, requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        T bodyContent = processResponse(rawResponse, responseType, options);
        return new ResponseEntity<>(
                bodyContent,
                rawResponse.getHeaders(),
                rawResponse.getStatusCode()
        );
    }

    // ----- Méthodes de traitement de la réponse -----
    private String processStringResponse(ResponseEntity<String> response, ApiOptions options) {
        if (Boolean.TRUE.equals(options.getReturnFullResponse())) {
            return response.getBody();
        }

        try {
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), 
                new TypeReference<Map<String, Object>>() {});
            
            Object data = extractData(responseMap, options.getDataPath());
            if (data != null) {
                return objectMapper.writeValueAsString(data);
            }
            return response.getBody();
        } catch (Exception e) {
            return response.getBody();
        }
    }
    
    private <T> T processResponse(ResponseEntity<Map<String, Object>> response,
                                JavaType responseType, ApiOptions options) {
        Map<String, Object> responseBody = response.getBody();

        if (Boolean.TRUE.equals(options.getReturnFullResponse())) {
            return objectMapper.convertValue(responseBody, responseType);
        }

        Object data = extractData(responseBody, options.getDataPath());
        return objectMapper.convertValue(data != null ? data : responseBody, responseType);
    }

    private Object extractData(Map<String, Object> responseBody, String dataPath) {
        if (dataPath == null || dataPath.isEmpty() || responseBody == null) {
            return responseBody;
        }

        String[] paths = dataPath.split("\\.");
        Object current = responseBody;

        for (String path : paths) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(path);
            if (current == null) return null;
        }

        return current;
    }

    // ----- Méthodes de construction d'URL -----
    private String buildUrl(String endpoint, String... params) {
        String baseUrl = apiUrl + endpoint;
        String queryParams = params.length > 0 ? "&" + String.join("&", params) : "";
        return baseUrl + (baseUrl.contains("?") ? "&" : "?") + queryParams;
    }

    private String buildFullUrl(String endpoint, String[] fields,
                              List<Filter> filters, String... params) {
        StringBuilder url = new StringBuilder(endpoint).append("?");
        
        if (fields != null && fields.length > 0) {
            url.append("fields=[")
               .append(Arrays.stream(fields)
                      .map(f -> "\"" + f + "\"")
                      .collect(Collectors.joining(",")))
               .append("]");
        }

        if (filters != null && !filters.isEmpty()) {
            if (fields != null && fields.length > 0) url.append("&");
            url.append("filters=[")
               .append(filters.stream()
                      .map(Filter::toJson)
                      .collect(Collectors.joining(",")))
               .append("]");
        }

        if (params.length > 0) {
            url.append("&").append(String.join("&", params));
        }

        return url.toString();
    }

    // ----- Méthodes utilitaires -----
    private ApiOptions initOptions(ApiOptions options) {
        if (options == null) {
            return new ApiOptions.Builder().build();
        }
        
        return new ApiOptions.Builder()
                .dataPath(options.getDataPath() != null ? options.getDataPath() : DEFAULT_DATA_PATH)
                .returnFullResponse(options.getReturnFullResponse() != null ? options.getReturnFullResponse() : false)
                .build();
    }
}