package mg.ando.erpnext.crm.service;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import mg.ando.erpnext.crm.config.Filter;

public interface ErpRestService {

    public static class ApiOptions {
        private String dataPath;
        private Integer pageLength;
        private Boolean returnFullResponse;

        public static class Builder {
            private final ApiOptions options = new ApiOptions();

            public Builder dataPath(String dataPath) {
                options.dataPath = dataPath;
                return this;
            }

            public Builder pageLength(Integer pageLength) {
                options.pageLength = pageLength;
                return this;
            }

            public Builder returnFullResponse(Boolean returnFullResponse) {
                options.returnFullResponse = returnFullResponse;
                return this;
            }

            public ApiOptions build() {
                return options;
            }
            
        }
        public static Builder builder() {
            return new Builder();
        }
        public String getDataPath() { return dataPath; }
        public Integer getPageLength() { return pageLength; }
        public Boolean getReturnFullResponse() { return returnFullResponse; }
    }

    // ========== METHODES AVEC OPTIONS ==========
    <T> T callApi(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        Class<T> responseType,
        ApiOptions options,
        String... params
    );

    <T> T callApi(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        TypeReference<T> responseType,
        ApiOptions options,
        String... params
    );

    <T> T callApiWithFilters(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        String[] fields,
        List<Filter> filters,
        Class<T> responseType,
        ApiOptions options,
        String... params
    );

    <T> T callApiWithFilters(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        String[] fields,
        List<Filter> filters,
        TypeReference<T> responseType,
        ApiOptions options,
        String... params
    );

    // ========== METHODES SANS OPTIONS ==========
    <T> T callApi(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        Class<T> responseType,
        String... params
    );

    <T> T callApi(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        TypeReference<T> responseType,
        String... params
    );

    <T> T callApiWithFilters(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        String[] fields,
        List<Filter> filters,
        Class<T> responseType,
        String... params
    );

    <T> T callApiWithFilters(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        String[] fields,
        List<Filter> filters,
        TypeReference<T> responseType,
        String... params
    );
    <T> ResponseEntity<T> callApiWithResponse(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        Class<T> responseType,
        ApiOptions options,
        String... params
    );

    <T> ResponseEntity<T> callApiWithResponse(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        TypeReference<T> responseType,
        ApiOptions options,
        String... params
    );

    // Versions sans options
    <T> ResponseEntity<T> callApiWithResponse(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        Class<T> responseType,
        String... params
    );

    <T> ResponseEntity<T> callApiWithResponse(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        HttpHeaders httpHeaders,
        TypeReference<T> responseType,
        String... params
    );
}