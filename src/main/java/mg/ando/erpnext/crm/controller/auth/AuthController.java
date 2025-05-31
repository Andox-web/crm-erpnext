package mg.ando.erpnext.crm.controller.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import mg.ando.erpnext.crm.service.auth.ErpAuthService;
import mg.ando.erpnext.crm.service.cookie.CookieService;

@Controller
public class AuthController {

    private final ErpAuthService authService;
    private final CookieService cookieService;

    public AuthController(ErpAuthService authService, CookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.logout();
        cookieService.expireSessionCookie(response);
        return "redirect:/login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        ResponseEntity<Map<String, Object>> result = authService.authenticate(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        );

        if (result.getStatusCode().is2xxSuccessful()) {
            String cookieToken = (String) result.getBody().get("cookie");
            String user = (String) result.getBody().get("user");
            cookieService.setSessionCookie(response, cookieToken);
            cookieService.setUserCookie(response, user);
        }
        return ResponseEntity.status(result.getStatusCode())
            .body(result.getBody());
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