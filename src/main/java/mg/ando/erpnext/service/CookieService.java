package mg.ando.erpnext.service;

import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    public void setSessionCookie(HttpServletResponse response, String token);
    public void expireSessionCookie(HttpServletResponse response);
}
