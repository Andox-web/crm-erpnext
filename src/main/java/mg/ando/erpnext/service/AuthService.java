package mg.ando.erpnext.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    public ResponseEntity<Map<String, Object>> authenticate(String username, String password);
    public void logout();
}
