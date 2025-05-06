package mg.ando.erpnext.controller.auth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Value("${erp.base-url}")
    private String ERP_BASE_URL;

    @Value("${erp.login-url}")
    private String ERP_LOGIN_URL;

    private static final int MAX_COOKIE_AGE = 7 * 24 * 60 * 60; // 1 semaine en secondes

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        // 1. Préparation de la requête
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String encodedBody = "usr=" + URLEncoder.encode(loginRequest.getUsername(), StandardCharsets.UTF_8) + 
                           "&pwd=" + URLEncoder.encode(loginRequest.getPassword(), StandardCharsets.UTF_8);
        
        HttpEntity<String> request = new HttpEntity<>(encodedBody, headers);

        // 2. Appel à l'API ERPNext
        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> erpResponse = restTemplate.exchange(
                ERP_BASE_URL+ERP_LOGIN_URL,
                HttpMethod.POST,
                request,
                Map.class
            );
            System.out.println(erpResponse.getHeaders().entrySet());
            // 3. Traitement de la réponse
            if (erpResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = erpResponse.getBody();
                String setCookieHeader = erpResponse.getHeaders().getFirst("Set-Cookie");
                setCookieHeader = setCookieHeader != null ? setCookieHeader.split(";")[0] : null;
                // Configuration du cookie sécurisé
                configureSessionCookie(response, setCookieHeader);
                
                // Réponse JSON structurée
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "user", responseBody.get("full_name")
                ));
            } else {
                System.out.println("Échec de l'authentification - Statut: {}"+ erpResponse.getStatusCode());
                return ResponseEntity.status(erpResponse.getStatusCode())
                    .body(Map.of("status", "error", "message", "Identifiants incorrects"));
            }

        } catch (HttpClientErrorException e) {
            System.out.println("Erreur client HTTP: {}"+ e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("status", "error", "message", parseErrorMessage(e)));
                
        } catch (ResourceAccessException e) {
            System.out.println("ERPNext inaccessible: {}"+ e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("status", "error", "message", "Service ERPNext indisponible"));
                
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur inattendue: {}"+ e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("status", "error", "message", "Erreur interne du serveur"));
        }
    }

    private void configureSessionCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("ERP_SESSION", token);
        cookie.setHttpOnly(true);
        cookie.setSecure("true".equals(System.getenv("PRODUCTION_MODE"))); // Auto-détection environnement
        cookie.setPath("/");
        cookie.setMaxAge(MAX_COOKIE_AGE);
        cookie.setAttribute("SameSite", "Lax"); // Protection CSRF
        
        // Configuration supplémentaire pour HTTPS
        if (cookie.getSecure()) {
            cookie.setAttribute("Partitioned", "true"); // Protection contre les attaques MITM
        }
        
        response.addCookie(cookie);
    }

    private String parseErrorMessage(HttpClientErrorException ex) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorBody = mapper.readValue(ex.getResponseBodyAsString(), Map.class);
            return (String) errorBody.getOrDefault("message", "Erreur d'authentification");
        } catch (Exception e) {
            return "Identifiants incorrects";
        }
    }

    public static class LoginRequest {
        private String username;
        
        private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
        
    }
}