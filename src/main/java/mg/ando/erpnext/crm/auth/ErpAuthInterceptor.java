package mg.ando.erpnext.crm.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;
import mg.ando.erpnext.crm.service.auth.ErpAuthService;

@Component
public class ErpAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private ErpAuthService erpAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (handler instanceof HandlerMethod handlerMethod) {
            boolean requiresAuth =
                    handlerMethod.getMethodAnnotation(RequireErpAuth.class) != null ||
                    handlerMethod.getBeanType().getAnnotation(RequireErpAuth.class) != null;

            if (!requiresAuth) return true;

            // Vérifie le cookie
            String sid = null;
            int count = 0;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("ERP_SESSION".equals(cookie.getName())) {
                        sid = cookie.getValue();
                        count++;
                    }
                    else if ("user".equals(cookie.getName())) {
                        request.setAttribute("user", cookie.getValue());
                        count++;
                    }
                    if (count == 2) {
                        break;
                    }
                }
            }

            // 1. Pas de session
            if (sid == null || sid.isBlank()) {
                return handleUnauthorized(request, response);
            }

            // 2. Session expirée mais renouvelable
            if (!erpAuthService.isSessionValid()) {    
                return handleUnauthorized(request, response);
            }
        }

        return true;
    }

    private boolean handleUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Session expirée ou non authentifié\"}");
        } else {
            response.sendRedirect("/login");
        }
        return false;
    }
}
