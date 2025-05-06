package mg.ando.erpnext.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService{

    @Value("${erp.base-url}")
    private String erpBaseUrl;

    @Value("${erp.login-url}")
    private String erpLoginUrl;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public AuthServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Map<String, Object>> authenticate(String username, String password) {
        try {
            HttpEntity<String> request = createLoginRequest(username, password);
            ResponseEntity<Map> response = restTemplate.exchange(
                erpBaseUrl + erpLoginUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            return handleAuthenticationResponse(response);
        } catch (HttpClientErrorException e) {
            return handleClientError(e);
        } catch (Exception e) {
            return handleGenericError(e);
        }
    }

    public void logout() {
        try {
            restTemplate.exchange(
                erpBaseUrl + "/api/method/logout",
                HttpMethod.GET,
                null,
                String.class
            );
        } catch (Exception e) {
            System.out.println("ERPNext logout error: " + e.getMessage());
        }
    }

    private HttpEntity<String> createLoginRequest(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String encodedBody = "usr=" + encodeValue(username) + 
                           "&pwd=" + encodeValue(password);
        
        return new HttpEntity<>(encodedBody, headers);
    }

    private String encodeValue(String value) {
        return value;
    }

    private ResponseEntity<Map<String, Object>> handleAuthenticationResponse(ResponseEntity<Map> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "user", response.getBody().get("full_name"),
                "cookie", extractSessionCookie(response.getHeaders())
            ));
        }
        return ResponseEntity.status(response.getStatusCode())
            .body(Map.of("status", "error", "message", "Authentication failed"));
    }

    private String extractSessionCookie(HttpHeaders headers) {
        String setCookieHeader = headers.getFirst(HttpHeaders.SET_COOKIE);
        return setCookieHeader != null ? setCookieHeader.split(";")[0] : null;
    }

    private ResponseEntity<Map<String, Object>> handleClientError(HttpClientErrorException e) {
        String errorMessage = parseErrorMessage(e);
        return ResponseEntity.status(e.getStatusCode())
            .body(Map.of("status", "error", "message", errorMessage));
    }

    private ResponseEntity<Map<String, Object>> handleGenericError(Exception e) {
        System.out.println("Unexpected error: " + e.getMessage());
        return ResponseEntity.internalServerError()
            .body(Map.of("status", "error", "message", "Internal server error"));
    }

    private String parseErrorMessage(HttpClientErrorException ex) {
        try {
            Map<String, Object> errorBody = objectMapper.readValue(ex.getResponseBodyAsString(), Map.class);
            return (String) errorBody.getOrDefault("message", "Authentication error");
        } catch (Exception e) {
            return "Invalid credentials";
        }
    }
}