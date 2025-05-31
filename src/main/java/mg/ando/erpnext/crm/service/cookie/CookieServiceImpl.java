package mg.ando.erpnext.crm.service.cookie;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieServiceImpl implements CookieService {

    @Value("${app.production-mode:false}")
    private boolean productionMode;

    private static final int MAX_COOKIE_AGE = 7 * 24 * 60 * 60;

    public void setSessionCookie(HttpServletResponse response, String token) {
        Cookie cookie = createBaseCookie("ERP_SESSION", token);
        cookie.setMaxAge(MAX_COOKIE_AGE);
        cookie.setSecure(productionMode);
        addCookieAttributes(cookie);
        response.addCookie(cookie);
    }

    public void expireSessionCookie(HttpServletResponse response) {
        Cookie cookie = createBaseCookie("ERP_SESSION", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private Cookie createBaseCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private void addCookieAttributes(Cookie cookie) {
        cookie.setAttribute("SameSite", "Lax");
        if (cookie.getSecure()) {
            cookie.setAttribute("Partitioned", "true");
        }
    }

    @Override
    public void setUserCookie(HttpServletResponse response, String user) {
        Cookie cookie = createBaseCookie("user", user);
        cookie.setMaxAge(MAX_COOKIE_AGE);
        cookie.setSecure(productionMode);
        addCookieAttributes(cookie);
        response.addCookie(cookie);
    }
}
