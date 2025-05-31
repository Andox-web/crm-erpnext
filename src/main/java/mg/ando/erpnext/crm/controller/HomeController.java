package mg.ando.erpnext.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import mg.ando.erpnext.crm.auth.annotation.RequireErpAuth;

@Controller
public class HomeController {

    @GetMapping("/")
    @RequireErpAuth
    public String home() {
        return "home";
    }

}
