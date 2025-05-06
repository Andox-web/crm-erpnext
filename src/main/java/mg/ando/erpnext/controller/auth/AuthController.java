package mg.ando.erpnext.controller.auth;

import mg.ando.erpnext.service.AuthService;
import mg.ando.erpnext.service.CookieService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    public AuthController(AuthService authService, CookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        ResponseEntity<Map<String, Object>> result = authService.authenticate(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );

        if (result.getStatusCode().is2xxSuccessful()) {
            String cookieToken = (String) result.getBody().get("cookie");
            cookieService.setSessionCookie(response, cookieToken);
        }

        return ResponseEntity.status(result.getStatusCode())
            .body(result.getBody());
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        authService.logout();
        cookieService.expireSessionCookie(response);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Logout successful"
        ));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        // Getters/Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}