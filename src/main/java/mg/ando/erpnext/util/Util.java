package mg.ando.erpnext.util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;

public class Util {
    public static HttpEntity<String> createErpRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        return new HttpEntity<>(headers);
    }
    public static HttpEntity<Map<String, Object>> createErpRequestWithBody(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString);
    }
    public static class PaginatedResponse<T> {
        private List<T> data;
        private int totalCount;
    
        public PaginatedResponse(List<T> data) {
            this.data = data;
            this.totalCount = totalCount;
        }
    
        public List<T> getData() {
            return data;
        }
    
        public void setData(List<T> data) {
            this.data = data;
        }
    
        public int getTotalCount() {
            return totalCount;
        }
    
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    
        
    }
    public static String getTextValue(JsonNode node, String fieldName) {
        return node.has(fieldName) ? node.get(fieldName).asText() : "";
    }
}
