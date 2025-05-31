package mg.ando.erpnext.crm.service.cookie;

import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    public void setSessionCookie(HttpServletResponse response, String token);
    public void expireSessionCookie(HttpServletResponse response);
    public void setUserCookie(HttpServletResponse response, String user);
}
