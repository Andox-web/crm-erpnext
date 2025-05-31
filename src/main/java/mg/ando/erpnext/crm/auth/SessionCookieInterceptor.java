package mg.ando.erpnext.crm.auth;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class SessionCookieInterceptor implements ClientHttpRequestInterceptor {

    private final String cookieName;

    public SessionCookieInterceptor(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    public ClientHttpResponse intercept(
        HttpRequest request,
        byte[] body,
        ClientHttpRequestExecution execution
    ) throws IOException {

        String cookieValue = extractCookieFromCurrentRequest();

        if (cookieValue != null) {
            request.getHeaders().add("Cookie", cookieValue);
        }

        return execution.execute(request, body);
    }

    private String extractCookieFromCurrentRequest() {
        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                if (request.getCookies() != null) {
                    for (Cookie cookie : request.getCookies()) {
                        if (cookieName.equals(cookie.getName())) {
                            return cookie.getValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
