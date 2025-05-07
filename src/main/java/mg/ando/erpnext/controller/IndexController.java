package mg.ando.erpnext.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import mg.ando.erpnext.auth.annotation.RequireERPAuth;
import mg.ando.erpnext.service.AuthService;
import mg.ando.erpnext.service.CookieService;

@Controller
public class IndexController {
    private AuthService authService;
    private CookieService cookieService;
    public IndexController(AuthService authService, CookieService cookieService) {
        this.authService = authService;
        this.cookieService = cookieService;
    }
    @GetMapping("/")
    @RequireERPAuth
    public String home(Model model) {
        model.addAttribute("title", "Home Page");
        return "home";
    }
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.logout();
        cookieService.expireSessionCookie(response);
        return "redirect:/login";
    }
}