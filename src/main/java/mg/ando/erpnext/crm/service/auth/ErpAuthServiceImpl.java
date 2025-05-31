package mg.ando.erpnext.crm.service.auth;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import mg.ando.erpnext.crm.service.ErpRestService;

@Service
public class ErpAuthServiceImpl implements ErpAuthService{

    private final String validateUrl = "/api/method/ping";

    private final String logoutUrl = "/api/method/logout";

    private final String loginUrl = "/api/method/login";

    private final ErpRestService erpRestService;

    

    public ErpAuthServiceImpl(ErpRestService erpRestService) {
        this.erpRestService = erpRestService;
    }

    public boolean isSessionValid() {
        try {
            ResponseEntity<String> response = erpRestService.callErpApiWithResponse(validateUrl, HttpMethod.GET, null,null, String.class);
            System.out.println(response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResponseEntity<Map<String, Object>> authenticate(String username, String password) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            String encodedBody = "usr=" + username + 
                            "&pwd=" + password;
            
            ResponseEntity<Map> response = erpRestService.callErpApiWithResponse(
                loginUrl,
                HttpMethod.POST,
                encodedBody,
                headers,
                Map.class
            );              
            return handleAuthenticationResponse(response);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
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
    
    public void logout() {
        try {
            erpRestService.callErpApiWithResponse(logoutUrl ,HttpMethod.GET,null, null,String.class);
        } catch (Exception e) {
            System.out.println("ERPNext logout error: " + e.getMessage());
        }
    }

}