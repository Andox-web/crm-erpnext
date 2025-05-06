package mg.ando.erpnext.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ERPAuthService {

    @Value("${erp.base-url}")
    private String erpBaseUrl;

    @Value("${erp.login-url}")
    private String loginUrl;

    @Value("${erp.validate-url}")
    private String validateUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isSessionValid(String sid) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", sid);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                erpBaseUrl + validateUrl,
                HttpMethod.GET,
                request,
                String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

