package mg.ando.erpnext.crm.service.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface ErpAuthService {
    public boolean isSessionValid();
    public ResponseEntity<Map<String, Object>> authenticate(String username, String password);
    public void logout();
}
