package mg.ando.erpnext.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import mg.ando.erpnext.auth.annotation.RequireERPAuth;

@Controller
public class IndexController {
    
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

}